import {Component} from '@angular/core';
import {Router} from '@angular/router'
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {HttpClient, HttpClientModule} from '@angular/common/http';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    HttpClientModule
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})

export class HomeComponent {
  constructor(private router: Router, private fb: FormBuilder, private http: HttpClient) {
    this.isUserLoggedIn = sessionStorage.getItem('loggedInUser') !== null;
    this.loggedInUserEmail = sessionStorage.getItem('loggedInUserEmail');
    this.isAdmin = sessionStorage.getItem('isAdmin') === 'true';
    if (this.isAdmin) {
      this.loadRequests()
    }

    this.movieCreateForm = this.initMovieForm()
    this.movieUpdateForm = this.initMovieForm()
    this.getMovies();
  }

  apiUrl = "http://localhost:2580/api/action"
  movieCreateForm: FormGroup
  movieUpdateForm: FormGroup
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
  loggedInUserEmail: string | null = ''
  menuOpen = 0
  title = 'Лабораторная работа'
  searchId: number | null = null;
  foundMovie: any = null;
  minDirector: any;
  taglineInput: string | undefined;
  moviesWithTagline: any[] = [];
  uniqueUsaBoxOffices: any[] = [];
  operatorsWithoutOscars: any[] = [];
  requests: any[] = []
  movies: any[] = []
  existingCoordinates: any[] = [];
  existingDirectors: any[] = [];
  existingScreenwriters: any[] = [];
  existingOperators: any[] = [];
  existingLocations: any[] = [];

  //TODO сделать потокобезопасной
  selectedMovieId: number | null = null;

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
    return this.fb.group({
      name: ['defaultName', Validators.required],
      creationDate: ["2024-10-15 14:30:55", Validators.required],
      oscarsCount: [1, [Validators.required, Validators.min(1)]],
      budget: [1, [Validators.required, Validators.min(1)]],
      totalBoxOffice: [1, [Validators.required, Validators.min(1)]],
      mpaaRating: ['G', Validators.required],
      length: [1, Validators.required],
      goldenPalmCount: [1, Validators.required],
      usaBoxOffice: [1, Validators.required],
      tagline: ['defaultTagline', Validators.required],
      genre: ['ACTION', Validators.required],
      coordinates: this.fb.group({
        id: ['', Validators.required],
        x: [0, Validators.required],
        y: [0, Validators.required]
      }),
      director: this.fb.group({
        id: ['', Validators.required],
        name: ['defaultName', Validators.required],
        eyeColor: ['BLACK', Validators.required],
        hairColor: ['BLACK', Validators.required],
        location: this.fb.group({
          id: ['', Validators.required],
          x: [0, Validators.required],
          y: [0, Validators.required],
          z: [0, Validators.required],
          name: ['defaultLocationName', Validators.required]
        }),
        height: [0, Validators.required],
        nationality: ['SPAIN', Validators.required]
      }),
      screenwriter: this.fb.group({
        id: ['', Validators.required],
        name: ['defaultName', Validators.required],
        eyeColor: ['BLACK', Validators.required],
        hairColor: ['BLACK', Validators.required],
        location: this.fb.group({
          id: ['', Validators.required],
          x: [0, Validators.required],
          y: [0, Validators.required],
          z: [0, Validators.required],
          name: ['defaultLocationName', Validators.required]
        }),
        height: [0, Validators.required],
        nationality: ['SPAIN', Validators.required]
      }),
      operator: this.fb.group({
        id: ['', Validators.required],
        name: ['defaultName', Validators.required],
        eyeColor: ['BLACK', Validators.required],
        hairColor: ['BLACK', Validators.required],
        location: this.fb.group({
          id: ['', Validators.required],
          x: [0, Validators.required],
          y: [0, Validators.required],
          z: [0, Validators.required],
          name: ['defaultLocationName', Validators.required]
        }),
        height: [0, Validators.required],
        nationality: ['SPAIN', Validators.required]
      })
    });
  }

  loadRequests() {
    this.http.get<any[]>('/api/notifications/pending')
      .subscribe(
        (data) => {
          this.requests = data;
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
    this.router.navigate(['/login']);
  }

  changeCreateFlag() {
    this.createFlag = this.createFlag ? 0 : 1
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

  toDoMethod() {
    //TODO
  }

  navigateTo(address: String) {
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

    this.movieUpdateForm .get('coordinates.id')?.setValue(coordinate.id);
    this.movieUpdateForm .get('coordinates.x')?.setValue(coordinate.x);
    this.movieUpdateForm .get('coordinates.y')?.setValue(coordinate.y);
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

  getMovies() {
    this.http.get<any[]>(this.apiUrl).subscribe((data: any[]) => {
      this.movies = data
    });
  }

  createMovie() {
    this.http.post(`http://localhost:2580/api/action/${sessionStorage.getItem('loggedInUserEmail')}`, this.movieCreateForm.value).subscribe(
      response => {
        this.getMovies()
        this.changeCreateFlag()
        this.initVars()
      },
      error => {
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

  deleteMovie() {
    return this.http.delete<any[]>(`${this.apiUrl}/${this.selectedMovieId}/${sessionStorage.getItem('loggedInUserEmail')}`).subscribe((data: any[]) => {
      this.movies = data
      this.changeDeleteFlag()
    });
  }

  openUpdateModal(id: any) {
    this.movieUpdateForm = this.initMovieForm()
    this.selectedMovieId = id
    this.changeUpdateFlag()
  }

  changeUpdateFlag() {
    this.updateFlag = this.updateFlag ? 0 : 1
  }

  updateMovie() {
    return this.http.put<any[]>(`${this.apiUrl}/${this.selectedMovieId}/${sessionStorage.getItem('loggedInUserEmail')}`, this.movieUpdateForm.value)
      .subscribe((data: any[]) => {
        this.movies = data;
        this.changeUpdateFlag();
        this.initVars();
      });
  }

  searchMovie() {
    this.http.get<any>(`${this.apiUrl}/${this.searchId}`).subscribe((data: any) => {
        console.log("start")
        if (data !== null) {
          this.foundMovie = data;
          console.log(11)
        } else {
          this.foundMovie = null
        }
        console.log(data)
      },
      (error) => {
        this.foundMovie = null;
        console.log(error)
      }
    )
  }

  approveRequest(id: number) {
    this.http.post(`/api/notifications/approve/${id}`, {})
      .subscribe(
        () => {
          alert("Application approved");
          this.loadRequests();
        },
        (error) => {
          alert("Error approving application:" + error.message);
        }
      );
  }

  isMovieCreator(movie: any): boolean {
    return movie.creator.email === sessionStorage.getItem('loggedInUserEmail');;
  }

  findDirectorWithMinMovies() {
    this.minDirectorFlag = true;
    return this.http.get<any>('/api/movies/min-director')
    .subscribe((data: any) => {
      this.minDirector = data;
      this.minDirectorFlag = false;
    });
  }

  searchByTagline() {
    // Вызов API для фильмов с Tagline больше чем заданное значение
    this.taglineSearchFlag = true;
  }

  findUniqueUsaBoxOffices() {
    this.uniqueUsaBoxOfficesFlag = true;
    // Вызов API для получения уникальных значений USA Box Office
  }

  findOperatorsWithoutOscars() {
    this.operatorsWithoutOscarsFlag = true;
    // Вызов API для поиска операторов без Оскаров
  }

  addOscarToRratedMovies() {
    this.addOscarFlag = true;
    // Вызов API для добавления "Оскара" фильмам с рейтингом R
  }
}
