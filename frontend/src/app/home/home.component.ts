import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router'
import {FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {Location} from '@angular/common';
import {AuthGuard} from '../auth.guard';
import {WebSocketService} from '../services/websocket';
import {BehaviorSubject} from 'rxjs';
import {FileLoaderComponent} from '../file-loader/file-loader.component';
import {Pagination} from './pagination';
import {ImportHistoryComponent} from '../import-history/import-history.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    HttpClientModule,
    AsyncPipe,
    FileLoaderComponent,
    ImportHistoryComponent
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./styles/button.css', "./styles/header.css", 'styles/layout.css', 'styles/modal.css', 'styles/table.css', 'styles/search-bar.css', 'styles/filter.css'],
  providers: [Pagination]
})

export class HomeComponent implements OnInit {
  constructor(private router: Router, private fb: FormBuilder, private http: HttpClient, private location: Location, private authGuard: AuthGuard, private webSocketService: WebSocketService, protected pagination: Pagination) {
    this.isUserLoggedIn = sessionStorage.getItem('loggedInUser') !== null;
    this.loggedInUserEmail = sessionStorage.getItem('loggedInUserEmail');
    this.isAdmin = sessionStorage.getItem('isAdmin') === 'true';
    if (this.isAdmin) {
      this.loadRequests()
    }
    this.movieCreateForm = this.initMovieForm()
    this.movieUpdateForm = this.initMovieForm()
  }

  ngOnInit() {
    this.pagination.loadMovies();

    history.pushState(null, document.title, location.href);
    window.addEventListener('popstate', function () {
      history.pushState(null, document.title, location.href);
    });

    this.webSocketService.connect();
    this.webSocketService.messages.subscribe((data) => {
      if (JSON.parse(data)) {
        this.pagination.loadMovies();
      }
    });
    this.filterForm.valueChanges.subscribe(() => this.filterTable());
  }

  apiUrl = "http://localhost:2580/api/action"
  movieCreateForm!: FormGroup
  movieUpdateForm!: FormGroup
  filterForm!: FormGroup
  createFlag = 0
  updateFlag = 0
  deleteFlag = 0
  searchFlag = 0
  notsFlag = 0
  minDirectorFlag = false;
  taglineSearchFlag = false;
  uniqueUsaBoxOfficesFlag = false;
  operatorsWithoutOscarsFlag = false;
  addOscarFlag = false;
  coordinatesVisible = false
  directorVisible = false
  screenwriterVisible = false
  operatorVisible = false
  directorLocationVisible = false
  screenwriterLocationVisible = false
  operatorLocationVisible = false
  isAdmin = false
  isUserLoggedIn: boolean = false
  notificationCount = 0
  loggedInUserEmail: string | null = ''
  menuOpen = 0
  title = 'Лабораторная работа'
  searchId: number | null = null;
  foundMovie: any = null;
  minDirector: any;
  taglineInput: string | undefined;
  public moviesWithTagline = new BehaviorSubject<any[]>([]);
  uniqueUsaBoxOffices: any[] = [];
  operatorsWithoutOscars: any[] = [];
  requests: any[] = [];
  existingCoordinates: any[] = [];
  existingDirectors: any[] = [];
  existingScreenwriters: any[] = [];
  existingOperators: any[] = [];
  existingLocations: any[] = [];
  selectedMovieId: number | null = null;
  deleteIdFlag = 0;
  idInput: number = 0;
  sortColumn: string = '';
  currentSortColumn: string | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';
  uniqueErrorMessage = '';

  initVars() {
    this.coordinatesVisible = false
    this.directorVisible = false
    this.screenwriterVisible = false
    this.operatorVisible = false
    this.directorLocationVisible = false
    this.screenwriterLocationVisible = false
    this.operatorLocationVisible = false

    this.existingCoordinates = [];
    this.existingDirectors = [];
    this.existingScreenwriters = [];
    this.existingOperators = [];
    this.existingLocations = [];

    this.movieCreateForm.get('coordinates.id')?.setValue('');
    this.movieCreateForm.get('director.id')?.setValue('');
    this.movieCreateForm.get('director.location.id')?.setValue('');
    this.movieCreateForm.get('screenwriter.id')?.setValue('');
    this.movieCreateForm.get('screenwriter.location.id')?.setValue('');
    this.movieCreateForm.get('operator.id')?.setValue('');
    this.movieCreateForm.get('operator.location.id')?.setValue('');

    this.movieUpdateForm.get('coordinates.id')?.setValue('');
    this.movieUpdateForm.get('director.id')?.setValue('');
    this.movieUpdateForm.get('director.location.id')?.setValue('');
    this.movieUpdateForm.get('screenwriter.id')?.setValue('');
    this.movieUpdateForm.get('screenwriter.location.id')?.setValue('');
    this.movieUpdateForm.get('operator.id')?.setValue('');
    this.movieUpdateForm.get('operator.location.id')?.setValue('');
  }

  initMovieForm() {
    this.filterForm = new FormGroup({
      nameFilter: new FormControl(''),
      creationDateFilter: new FormControl(''),
      taglineFilter: new FormControl('')
    });
    return this.fb.group({
      name: ['defaultName', [Validators.required, Validators.minLength(1)]],
      creationDate: ["2024-10-15 14:30:55", [Validators.required]],
      oscarsCount: [1, [Validators.required, Validators.min(1)]],
      budget: [1, [Validators.required, Validators.min(1)]],
      totalBoxOffice: [1, [Validators.min(1)]],
      mpaaRating: ['G', [Validators.required]],
      length: [1, [Validators.required, Validators.min(1)]],
      goldenPalmCount: [1, [Validators.min(1)]],
      usaBoxOffice: [1, [Validators.required, Validators.min(1)]],
      tagline: ['defaultTagline', [Validators.required]],
      genre: ['ACTION'],
      coordinates: this.fb.group({
        id: [''],
        x: [0, [Validators.required]],
        y: [0, [Validators.required]]
      }),
      director: this.fb.group({
        id: [''],
        name: ['defaultName', [Validators.required, Validators.minLength(1)]],
        eyeColor: ['BLACK', [Validators.required]],
        hairColor: ['BLACK', [Validators.required]],
        location: this.fb.group({
          id: [''],
          x: [0, [Validators.required]],
          y: [0, [Validators.required]],
          z: [0, [Validators.required]],
          name: ['defaultLocationName', [Validators.required, Validators.minLength(1)]]
        }),
        height: [1, [Validators.required, Validators.min(1)]],
        nationality: ['SPAIN']
      }),
      screenwriter: this.fb.group({
        id: [''],
        name: ['defaultName', [Validators.required, Validators.minLength(1)]],
        eyeColor: ['BLACK', [Validators.required]],
        hairColor: ['BLACK', [Validators.required]],
        location: this.fb.group({
          id: [''],
          x: [0, [Validators.required]],
          y: [0, [Validators.required]],
          z: [0, [Validators.required]],
          name: ['defaultLocationName', [Validators.required, Validators.minLength(1)]]
        }),
        height: [1, [Validators.required, Validators.min(1)]],
        nationality: ['SPAIN']
      }),
      operator: this.fb.group({
        id: [''],
        name: ['defaultName', [Validators.required, Validators.minLength(1)]],
        eyeColor: ['BLACK', [Validators.required]],
        hairColor: ['BLACK', [Validators.required]],
        location: this.fb.group({
          id: [''],
          x: [0, [Validators.required]],
          y: [0, [Validators.required]],
          z: [0, [Validators.required]],
          name: ['defaultLocationName', [Validators.required, Validators.minLength(1)]]
        }),
        height: [1, [Validators.required, Validators.min(1)]],
        nationality: ['SPAIN']
      })
    });
  }

  loadRequests() {
    this.http.get<any[]>('/api/notifications/pending')
      .subscribe(
        (data) => {
          this.requests = data;
          this.notificationCount = data.length
        },
        (error) => {
          console.error("Ошибка при загрузке уведомлений:", error);
        }
      );
  }

  logout() {
    sessionStorage.clear();
    this.isUserLoggedIn = false;
    this.isAdmin = false;
    this.loggedInUserEmail = null;
    this.authGuard.markProgrammaticNavigation();
    this.router.navigate(['/login']);
  }

  changeCreateFlag() {
    this.movieCreateForm.get("creationDate")?.setValue(this.setCurrentTime())
    this.createFlag = this.createFlag ? 0 : 1
  }

  setCurrentTime(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }

  getFormErrors(command: String): string[] {
    const errors: string[] = [];

    const errorMessages: { [key: string]: string } = {
      required: 'Поле обязательно для заполнения',
      min: 'Значение должно быть больше или равно минимальному допустимому',
      minLength: 'Длина должна быть больше или равна минимальной допустимой'
    };

    function getErrors(control: any, prefix: string) {
      if (control instanceof FormGroup) {
        Object.keys(control.controls).forEach((key) => {
          getErrors(control.get(key), `${prefix}${key}.`);
        });
      } else if (control.errors) {
        Object.keys(control.errors).forEach((errorKey) => {
          const errorMessage = errorMessages[errorKey] || `Ошибка: ${errorKey}`;
          errors.push(`${prefix.slice(0, -1)}: ${errorMessage}`);
        });
      }
    }
    if (command === 'create') {
      getErrors(this.movieCreateForm, '');
    } else if (command === 'update') {
      getErrors(this.movieUpdateForm, '')
    }
    return errors;
  }

  changeSearchFlag() {
    this.searchFlag = this.searchFlag ? 0 : 1
  }

  changeMenuOpen() {
    this.menuOpen = this.menuOpen ? 0 : 1
  }

  changeNotsFlag() {
    this.notsFlag = this.notsFlag ? 0 : 1
  }

  changeTaglineSearchFlag() {
    this.taglineSearchFlag = !this.taglineSearchFlag;
  }

  navigateTo(address: String) {
    this.authGuard.markProgrammaticNavigation();
    this.router.navigate([address])
  }

  toggleCoordinates() {
    this.coordinatesVisible = !this.coordinatesVisible;
  }

  loadExistingCoordinates() {
    this.http.get('/api/coordinates').subscribe(
      (response: any) => {
        this.existingCoordinates = response;
      },
      (error) => {
        console.error("Error loading coordinates:", error);
      }
    );
  }

  loadExistingPersons() {
    this.http.get('/api/persons').subscribe(
      (response: any) => {
        this.existingDirectors = response;
        this.existingScreenwriters = response;
        this.existingOperators = response;
      },
      (error) => {
        console.error("Error loading persons:", error);
      }
    );
  }

  loadExistingLocations() {
    this.http.get('/api/locations').subscribe(
      (response: any) => {
        this.existingLocations = response;
      },
      (error) => {
        console.error("Error loading locations:", error);
      }
    );
  }

  selectCoordinate(coordinate: any) {
    this.movieCreateForm.get('coordinates.id')?.setValue(coordinate.id);
    this.movieCreateForm.get('coordinates.x')?.setValue(coordinate.x);
    this.movieCreateForm.get('coordinates.y')?.setValue(coordinate.y);

    this.movieUpdateForm.get('coordinates.id')?.setValue(coordinate.id);
    this.movieUpdateForm.get('coordinates.x')?.setValue(coordinate.x);
    this.movieUpdateForm.get('coordinates.y')?.setValue(coordinate.y);
  }

  selectDirector(director: any) {
    this.movieCreateForm.get('director.id')?.setValue(director.id);
    this.movieCreateForm.get('director.name')?.setValue(director.name);
    this.movieCreateForm.get('director.eyeColor')?.setValue(director.eyeColor);
    this.movieCreateForm.get('director.hairColor')?.setValue(director.hairColor);
    this.movieCreateForm.get('director.height')?.setValue(director.height);
    this.movieCreateForm.get('director.nationality')?.setValue(director.nationality);

    this.movieUpdateForm.get('director.id')?.setValue(director.id);
    this.movieUpdateForm.get('director.name')?.setValue(director.name);
    this.movieUpdateForm.get('director.eyeColor')?.setValue(director.eyeColor);
    this.movieUpdateForm.get('director.hairColor')?.setValue(director.hairColor);
    this.movieUpdateForm.get('director.height')?.setValue(director.height);
    this.movieUpdateForm.get('director.nationality')?.setValue(director.nationality);
  }

  selectDirectorLocation(location: any) {
    this.movieCreateForm.get('director.location.id')?.setValue(location.id);
    this.movieCreateForm.get('director.location.x')?.setValue(location.x);
    this.movieCreateForm.get('director.location.y')?.setValue(location.y);
    this.movieCreateForm.get('director.location.z')?.setValue(location.z);
    this.movieCreateForm.get('director.location.name')?.setValue(location.name);

    this.movieUpdateForm.get('director.location.id')?.setValue(location.id);
    this.movieUpdateForm.get('director.location.x')?.setValue(location.x);
    this.movieUpdateForm.get('director.location.y')?.setValue(location.y);
    this.movieUpdateForm.get('director.location.z')?.setValue(location.z);
    this.movieUpdateForm.get('director.location.name')?.setValue(location.name);
  }

  selectScreenwriter(screenwriter: any) {
    this.movieCreateForm.get('screenwriter.id')?.setValue(screenwriter.id);
    this.movieCreateForm.get('screenwriter.name')?.setValue(screenwriter.name);
    this.movieCreateForm.get('screenwriter.eyeColor')?.setValue(screenwriter.eyeColor);
    this.movieCreateForm.get('screenwriter.hairColor')?.setValue(screenwriter.hairColor);
    this.movieCreateForm.get('screenwriter.height')?.setValue(screenwriter.height);
    this.movieCreateForm.get('screenwriter.nationality')?.setValue(screenwriter.nationality);

    this.movieUpdateForm.get('screenwriter.id')?.setValue(screenwriter.id);
    this.movieUpdateForm.get('screenwriter.name')?.setValue(screenwriter.name);
    this.movieUpdateForm.get('screenwriter.eyeColor')?.setValue(screenwriter.eyeColor);
    this.movieUpdateForm.get('screenwriter.hairColor')?.setValue(screenwriter.hairColor);
    this.movieUpdateForm.get('screenwriter.height')?.setValue(screenwriter.height);
    this.movieUpdateForm.get('screenwriter.nationality')?.setValue(screenwriter.nationality);
  }

  selectScreenwriterLocation(location: any) {
    this.movieCreateForm.get('screenwriter.location.id')?.setValue(location.id);
    this.movieCreateForm.get('screenwriter.location.x')?.setValue(location.x);
    this.movieCreateForm.get('screenwriter.location.y')?.setValue(location.y);
    this.movieCreateForm.get('screenwriter.location.z')?.setValue(location.z);
    this.movieCreateForm.get('screenwriter.location.name')?.setValue(location.name);

    this.movieUpdateForm.get('screenwriter.location.id')?.setValue(location.id);
    this.movieUpdateForm.get('screenwriter.location.x')?.setValue(location.x);
    this.movieUpdateForm.get('screenwriter.location.y')?.setValue(location.y);
    this.movieUpdateForm.get('screenwriter.location.z')?.setValue(location.z);
    this.movieUpdateForm.get('screenwriter.location.name')?.setValue(location.name);
  }

  selectOperator(operator: any) {
    this.movieCreateForm.get('operator.id')?.setValue(operator.id);
    this.movieCreateForm.get('operator.name')?.setValue(operator.name);
    this.movieCreateForm.get('operator.eyeColor')?.setValue(operator.eyeColor);
    this.movieCreateForm.get('operator.hairColor')?.setValue(operator.hairColor);
    this.movieCreateForm.get('operator.height')?.setValue(operator.height);
    this.movieCreateForm.get('operator.nationality')?.setValue(operator.nationality);

    this.movieUpdateForm.get('operator.id')?.setValue(operator.id);
    this.movieUpdateForm.get('operator.name')?.setValue(operator.name);
    this.movieUpdateForm.get('operator.eyeColor')?.setValue(operator.eyeColor);
    this.movieUpdateForm.get('operator.hairColor')?.setValue(operator.hairColor);
    this.movieUpdateForm.get('operator.height')?.setValue(operator.height);
    this.movieUpdateForm.get('operator.nationality')?.setValue(operator.nationality);
  }

  selectOperatorLocation(location: any) {
    this.movieCreateForm.get('operator.location.id')?.setValue(location.id);
    this.movieCreateForm.get('operator.location.x')?.setValue(location.x);
    this.movieCreateForm.get('operator.location.y')?.setValue(location.y);
    this.movieCreateForm.get('operator.location.z')?.setValue(location.z);
    this.movieCreateForm.get('operator.location.name')?.setValue(location.name);

    this.movieUpdateForm.get('operator.location.id')?.setValue(location.id);
    this.movieUpdateForm.get('operator.location.x')?.setValue(location.x);
    this.movieUpdateForm.get('operator.location.y')?.setValue(location.y);
    this.movieUpdateForm.get('operator.location.z')?.setValue(location.z);
    this.movieUpdateForm.get('operator.location.name')?.setValue(location.name);
  }

  toggleDirector() {
    this.directorVisible = !this.directorVisible;
  }

  toggleDirectorLocation() {
    this.directorLocationVisible = !this.directorLocationVisible;
  }

  toggleScreenwriter() {
    this.screenwriterVisible = !this.screenwriterVisible;
  }

  toggleScreenwriterLocation() {
    this.screenwriterLocationVisible = !this.screenwriterLocationVisible;
  }

  toggleOperator() {
    this.operatorVisible = !this.operatorVisible;
  }

  toggleOperatorLocation() {
    this.operatorLocationVisible = !this.operatorLocationVisible;
  }

  createMovie() {
    this.http.post(`http://localhost:2580/api/action/${sessionStorage.getItem('loggedInUserEmail')}`, this.movieCreateForm.value).subscribe(
      response => {
        this.changeCreateFlag()
        this.initVars()
      },
      error => {
        this.uniqueErrorMessage = error.error.message
        console.error('Error creating movie', error)
      });
  }

  openDeleteModal(id: number) {
    this.selectedMovieId = id;
    this.changeDeleteFlag();
  }

  changeDeleteFlag() {
    this.deleteFlag = this.deleteFlag ? 0 : 1
  }

  changeDeleteIdFlag() {
    this.deleteIdFlag = this.deleteIdFlag ? 0 : 1
  }

  deleteMovie(id: number) {
    return this.http.delete<any[]>(`${this.apiUrl}/${id}/${this.selectedMovieId}/${sessionStorage.getItem('loggedInUserEmail')}`).subscribe((data: any[]) => {
      this.changeDeleteFlag()
      this.changeDeleteIdFlag()
    });
  }

  openUpdateModal(id: any) {
    this.movieUpdateForm = this.initMovieForm()
    this.selectedMovieId = id
    this.changeUpdateFlag()
  }

  changeUpdateFlag() {
    this.movieUpdateForm.get("creationDate")?.setValue(this.setCurrentTime())
    this.updateFlag = this.updateFlag ? 0 : 1
  }

  updateMovie() {
    return this.http.put<any[]>(`${this.apiUrl}/${this.selectedMovieId}/${sessionStorage.getItem('loggedInUserEmail')}`, this.movieUpdateForm.value)
      .subscribe((data: any[]) => {
        this.changeUpdateFlag();
        this.initVars();
      });
  }

  searchMovie() {
    this.http.get<any>(`${this.apiUrl}/${this.searchId}`).subscribe((data: any) => {
        if (data !== null) {
          this.foundMovie = data;
        } else {
          this.foundMovie = null
        }
      },
      (error) => {
        this.foundMovie = null;
      }
    )
  }

  approveRequest(id: number) {
    this.http.post(`/api/notifications/approve/${id}`, {})
      .subscribe(
        () => {
          alert("Request approve");
          this.loadRequests();
        },
        (error) => {
          alert("Error approving application:" + error.message);
        }
      );
  }

  isMovieCreator(movie: any): boolean {
    return movie.creator.email === sessionStorage.getItem('loggedInUserEmail');
  }

  findDirectorWithMinMovies() {
    this.minDirectorFlag = true;
    this.http.get<any>('/api/min-director')
      .subscribe((data: any) => {
        this.minDirector = data
      });
  }

  searchByTagline(tagline: string | undefined) {
    this.taglineSearchFlag = true;
    if (tagline) {
      this.http.get<any[]>('/api/tagline-greater-than', {
        params: {tagline}
      }).subscribe((data: any[]) => {
        this.moviesWithTagline.next(data)
      });
    }
  }


  findUniqueUsaBoxOffices() {
    this.uniqueUsaBoxOfficesFlag = true;
    this.http.get<number[]>('/api/unique-usa-box-office')
      .subscribe((data: any[]) => {
        this.uniqueUsaBoxOffices = data
      });
  }

  findOperatorsWithoutOscars() {
    this.operatorsWithoutOscarsFlag = true;
    this.http.get<any[]>('/api/operators-no-oscars')
      .subscribe((data: any[]) => {
        this.operatorsWithoutOscars = data
      });
  }

  addOscarToRratedMovies() {
    this.addOscarFlag = true;
    this.http.post(`/api/add-oscar-to-r-rated`, {})
      .subscribe(
        () => {
        },
        (error) => {
        }
      );
  }

  sortTable(column: string) {
    if (this.currentSortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.currentSortColumn = column;
      this.sortDirection = 'asc';
    }

    const sortedMovies = this.pagination.movies.getValue().sort((a, b) => {
      const aValue = a[column];
      const bValue = b[column];
      if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });

    this.pagination.movies.next(sortedMovies);
  }

  isSorted(column: string, direction: 'asc' | 'desc'): boolean {
    return this.currentSortColumn === column && this.sortDirection === direction;
  }

  filterTable() {
    const { nameFilter, creationDateFilter, taglineFilter } = this.filterForm.value;
    const filteredMovies = this.pagination.allMovies.getValue().filter(movie => {
      return (
        (nameFilter ? movie.name.includes(nameFilter) : true) &&
        (creationDateFilter ? movie.creationDate.includes(creationDateFilter) : true) &&
        (taglineFilter ? movie.tagline.includes(taglineFilter) : true)
      );
    });

    this.pagination.movies.next(filteredMovies);
  }

  clear() {
    return this.http.delete<any[]>(`${this.apiUrl}/${sessionStorage.getItem('loggedInUserEmail')}`).subscribe((data: any[]) => {
    });
  }
}
