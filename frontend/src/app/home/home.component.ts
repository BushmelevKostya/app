import { Component } from '@angular/core';
import { Router } from '@angular/router'
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
    this.movieForm = this.fb.group({
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
    this.ngOnInit();
  }

  movieForm: FormGroup
  createFlag = 0
  updateFlag = 0
  deleteFlag = 0
  searchFlag = 0
  notsFlag = 0
  coordinatesVisible = false
  directorVisible = false
  screenwriterVisible = false
  operatorVisible = false
  isAdmin = 1
  menuOpen = 0
  title = 'Лабораторная работа'
  searchId: number | null = null;
  request = {
    id: 1,
    userEmail: "kosbush@gmail.com"
  }
  requests = [this.request]

  movie = {
    id: 1,
    name: 'Чип и Дейл',
    coordinates: { x: 0, y: 0 },
    oscarsCount: 3,
    budget: 1000000000,
    totalBoxOffice: 100000000000000,
    mpaaRating: 'Самый крутой',
    director: { name: '', eyeColor: '', hairColor: '', location: { x: 0, y: 0, z: 0, name: '' }, height: 0, nationality: '' },
    screenwriter: { name: '', eyeColor: '', hairColor: '', location: { x: 0, y: 0, z: 0, name: '' }, height: 0, nationality: '' },
    operator: { name: '', eyeColor: '', hairColor: '', location: { x: 0, y: 0, z: 0, name: '' }, height: 0, nationality: '' },
    length: 0,
    goldenPalmCount: 0,
    usaBoxOffice: null,
    tagline: '',
    genre: '',
    creationDate: "23"
  };

  movies = [this.movie];

  createMovie() {
    if (this.movieForm.valid) {
      this.http.post('http://localhost:8080/api/create', this.movieForm.value).subscribe(
        response => {
          console.log('Movie created successfully', response)
        },
        error => {
          console.error('Error creating movie', error)
        });
    } else {
      console.error("Form is invalid")
    }
  }

  changeCreateFlag() {
    this.createFlag = this.createFlag ? 0 : 1
  }

  changeUpdateFlag() {
    this.updateFlag = this.updateFlag ? 0 : 1
  }

  changeDeleteFlag() {
    this.deleteFlag = this.deleteFlag ? 0 : 1
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

  searchMovie() {
  }

  updateMovie(id: number) {
  }

  deleteMovie(id: number) {
  }

  toDoMethod() {
    //TODO
  }

  sortTable(str: String) {
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

  loadExistingDirector() {
  }

  loadExistingCoordinates() {
  }

  toggleScreenwriter() {
    this.screenwriterVisible = !this.screenwriterVisible;
  }

  loadExistingScreenwriter() {
  }

  toggleOperator() {
    this.operatorVisible = !this.operatorVisible;
  }

  loadExistingOperator() {
  }

  ngOnInit() {
    this.movieForm.valueChanges.subscribe((newValue) => {
      this.updateMovies(newValue);
    });
  }

  updateMovies(data: any) {
    this.movies = [{
      name: data.name,
      creationDate: data.creationDate,
      oscarsCount: data.oscarsCount,
      budget: data.budget,
      totalBoxOffice: data.totalBoxOffice,
      mpaaRating: data.mpaaRating,
      length: data.length,
      goldenPalmCount: data.goldenPalmCount,
      usaBoxOffice: data.usaBoxOffice,
      tagline: data.tagline,
      genre: data.genre,
      coordinates: data.coordinates,
      director: data.director,
      screenwriter: data.screenwriter,
      operator: data.operator,
      id: 0
    }];
  }
}
