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
    this.isAdmin = sessionStorage.getItem('isAdmin') === 'true'
    if (this.isAdmin) {
      this.loadRequests()
    }

    this.movieCreateForm = this.initMovieForm()
    this.getMovies();
    this.movieUpdateForm = this.initMovieForm()
  }

  apiUrl = "http://localhost:8080/api/action"
  movieCreateForm: FormGroup
  movieUpdateForm: FormGroup
  createFlag = 0
  updateFlag = 0
  deleteFlag = 0
  searchFlag = 0
  notsFlag = 0
  coordinatesVisible = false
  directorVisible = false
  screenwriterVisible = false
  operatorVisible = false
  isAdmin = false
  menuOpen = 0
  title = 'Лабораторная работа'
  searchId: number | null = null;
  foundMovie: any = null;
  // request = {
  //   id: 1,
  //   userEmail: "kosbush@gmail.com"
  // }
  requests: any[] = []

  movie = {
    id: 0,
    name: 'Чип и Дейл',
    coordinates: {x: 0, y: 0},
    oscarsCount: 3,
    budget: 1000000000,
    totalBoxOffice: 100000000000000,
    mpaaRating: 'Самый крутой',
    director: {
      name: '1',
      eyeColor: '1',
      hairColor: '1',
      location: {x: 0, y: 0, z: 0, name: '1'},
      height: 0,
      nationality: '1'
    },
    screenwriter: {
      name: '1',
      eyeColor: '1',
      hairColor: '1',
      location: {x: 0, y: 0, z: 0, name: '1'},
      height: 0,
      nationality: '1'
    },
    operator: {
      name: '1',
      eyeColor: '1',
      hairColor: '1',
      location: {x: 0, y: 0, z: 0, name: '1'},
      height: 0,
      nationality: '1'
    },
    length: 0,
    goldenPalmCount: 0,
    usaBoxOffice: null,
    tagline: '1',
    genre: '1',
    creationDate: "23"
  };

  movies: any[] = [this.movie, this.movie]
  existingCoordinates: any[] = [];
  existingDirectors: any[] = [];
  existingScreenwriters: any[] = [];
  existingOperators: any[] = [];

  //TODO сделать потокобезопасной
  selectedMovieId: number | null = null;

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
        name: ['defaultName', Validators.required],
        eyeColor: ['BLACK', Validators.required],
        hairColor: ['BLACK', Validators.required],
        location: this.fb.group({
          x: [0, Validators.required],
          y: [0, Validators.required],
          z: [0, Validators.required],
          name: ['defaultLocationName', Validators.required]
        }),
        height: [0, Validators.required],
        nationality: ['SPAIN', Validators.required]
      }),
      screenwriter: this.fb.group({
        name: ['defaultName', Validators.required],
        eyeColor: ['BLACK', Validators.required],
        hairColor: ['BLACK', Validators.required],
        location: this.fb.group({
          x: [0, Validators.required],
          y: [0, Validators.required],
          z: [0, Validators.required],
          name: ['defaultLocationName', Validators.required]
        }),
        height: [0, Validators.required],
        nationality: ['SPAIN', Validators.required]
      }),
      operator: this.fb.group({
        name: ['defaultName', Validators.required],
        eyeColor: ['BLACK', Validators.required],
        hairColor: ['BLACK', Validators.required],
        location: this.fb.group({
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

  toDoMethod() {
    //TODO
  }

  navigateTo(address: String) {
    this.router.navigate([address])
  }

  toggleCoordinates() {
    this.coordinatesVisible = !this.coordinatesVisible;
  }

  toggleDirector() {
    this.directorVisible = !this.directorVisible;
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

  selectCoordinate(coordinate: any) {
    this.movieCreateForm.get('coordinates.id')?.setValue(coordinate.id);
    this.movieCreateForm.get('coordinates.x')?.setValue(coordinate.x);
    this.movieCreateForm.get('coordinates.y')?.setValue(coordinate.y);
  }

  selectDirector(director: any) {
    this.movieCreateForm.get('director.name')?.setValue(director.name);
    this.movieCreateForm.get('director.eyeColor')?.setValue(director.eyeColor);
    this.movieCreateForm.get('director.hairColor')?.setValue(director.hairColor);
    this.movieCreateForm.get('director.location.x')?.setValue(director.location.x);
    this.movieCreateForm.get('director.location.y')?.setValue(director.location.y);
    this.movieCreateForm.get('director.location.z')?.setValue(director.location.z);
    this.movieCreateForm.get('director.location.name')?.setValue(director.location.name);
    this.movieCreateForm.get('director.height')?.setValue(director.height);
    this.movieCreateForm.get('director.nationality')?.setValue(director.nationality);
  }

  selectScreenwriter(screenwriter: any) {
    this.movieCreateForm.get('screenwriter.name')?.setValue(screenwriter.name);
    this.movieCreateForm.get('screenwriter.eyeColor')?.setValue(screenwriter.eyeColor);
    this.movieCreateForm.get('screenwriter.hairColor')?.setValue(screenwriter.hairColor);
    this.movieCreateForm.get('screenwriter.location.x')?.setValue(screenwriter.location.x);
    this.movieCreateForm.get('screenwriter.location.y')?.setValue(screenwriter.location.y);
    this.movieCreateForm.get('screenwriter.location.z')?.setValue(screenwriter.location.z);
    this.movieCreateForm.get('screenwriter.location.name')?.setValue(screenwriter.location.name);
    this.movieCreateForm.get('screenwriter.height')?.setValue(screenwriter.height);
    this.movieCreateForm.get('screenwriter.nationality')?.setValue(screenwriter.nationality);
  }

  selectOperator(operator: any) {
    this.movieCreateForm.get('operator.name')?.setValue(operator.name);
    this.movieCreateForm.get('operator.eyeColor')?.setValue(operator.eyeColor);
    this.movieCreateForm.get('operator.hairColor')?.setValue(operator.hairColor);
    this.movieCreateForm.get('operator.location.x')?.setValue(operator.location.x);
    this.movieCreateForm.get('operator.location.y')?.setValue(operator.location.y);
    this.movieCreateForm.get('operator.location.z')?.setValue(operator.location.z);
    this.movieCreateForm.get('operator.location.name')?.setValue(operator.location.name);
    this.movieCreateForm.get('operator.height')?.setValue(operator.height);
    this.movieCreateForm.get('operator.nationality')?.setValue(operator.nationality);
  }

  toggleScreenwriter() {
    this.screenwriterVisible = !this.screenwriterVisible;
  }

  toggleOperator() {
    this.operatorVisible = !this.operatorVisible;
  }

  getMovies() {
    this.http.get<any[]>(this.apiUrl).subscribe((data: any[]) => {
      this.movies = data
    });
  }

  createMovie() {
    // this.movieCreateForm.value.coordinates.id = -1
    this.http.post('http://localhost:8080/api/action', this.movieCreateForm.value).subscribe(
      response => {
        this.getMovies()
        this.changeCreateFlag()
      },
      error => {
        console.error('Error creating movie', error)
      });
    // }
  }

  openDeleteModal(id: number) {
    this.selectedMovieId = id;
    this.changeDeleteFlag();
  }

  changeDeleteFlag() {
    this.deleteFlag = this.deleteFlag ? 0 : 1
  }

  deleteMovie() {
    return this.http.delete<any[]>(`${this.apiUrl}/${this.selectedMovieId}`).subscribe((data: any[]) => {
      this.movies = data
      this.changeDeleteFlag()
    });
  }

  openUpdateModal(movie: any) {
    this.movieUpdateForm = this.fb.group({
      name: [movie.name, Validators.required],
      creationDate: [movie.creationDate, Validators.required],
      oscarsCount: [movie.oscarsCount, [Validators.required, Validators.min(1)]],
      budget: [movie.budget, [Validators.required, Validators.min(1)]],
      totalBoxOffice: [movie.totalBoxOffice, [Validators.required, Validators.min(1)]],
      mpaaRating: [movie.mpaaRating, Validators.required],
      length: [movie.length, Validators.required],
      goldenPalmCount: [movie.goldenPalmCount, Validators.required],
      usaBoxOffice: [movie.usaBoxOffice, Validators.required],
      tagline: [movie.tagline, Validators.required],
      genre: [movie.genre, Validators.required],
      coordinates: this.fb.group({
        x: [movie.coordinates.x, Validators.required],
        y: [movie.coordinates.y, Validators.required]
      }),
      director: this.fb.group({
        name: [movie.director.name, Validators.required],
        eyeColor: [movie.director.eyeColor, Validators.required],
        hairColor: [movie.director.hairColor, Validators.required],
        location: this.fb.group({
          x: [movie.director.location.x, Validators.required],
          y: [movie.director.location.y, Validators.required],
          z: [movie.director.location.z, Validators.required],
          name: [movie.director.location.name, Validators.required]
        }),
        height: [movie.director.height, Validators.required],
        nationality: [movie.director.nationality, Validators.required]
      }),
      screenwriter: this.fb.group({
        name: [movie.screenwriter.name, Validators.required],
        eyeColor: [movie.screenwriter.eyeColor, Validators.required],
        hairColor: [movie.screenwriter.hairColor, Validators.required],
        location: this.fb.group({
          x: [movie.screenwriter.location.x, Validators.required],
          y: [movie.screenwriter.location.y, Validators.required],
          z: [movie.screenwriter.location.z, Validators.required],
          name: [movie.screenwriter.location.name, Validators.required]
        }),
        height: [movie.screenwriter.height, Validators.required],
        nationality: [movie.screenwriter.nationality, Validators.required]
      }),
      operator: this.fb.group({
        name: [movie.operator.name, Validators.required],
        eyeColor: [movie.operator.eyeColor, Validators.required],
        hairColor: [movie.operator.hairColor, Validators.required],
        location: this.fb.group({
          x: [movie.operator.location.x, Validators.required],
          y: [movie.operator.location.y, Validators.required],
          z: [movie.operator.location.z, Validators.required],
          name: [movie.operator.location.name, Validators.required]
        }),
        height: [movie.operator.height, Validators.required],
        nationality: [movie.operator.nationality, Validators.required]
      })
    });
    this.changeUpdateFlag()
  }

  changeUpdateFlag() {
    this.updateFlag = this.updateFlag ? 0 : 1
  }

  updateMovie() {
    return this.http.put<any[]>(`${this.apiUrl}`, this.movieUpdateForm.value).subscribe((data: any[]) => {
      this.movies = data
      this.changeUpdateFlag()
    })
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
}
