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
  constructor( private router: Router, private fb: FormBuilder, private http: HttpClient ) {
    this.movieForm = this.fb.group({
      name: ['', Validators.required],
      oscarsCount: [0, [Validators.required, Validators.min(1)]],
      budget: [0, [Validators.required, Validators.min(1)]],
      totalBoxOffice: [0, [Validators.required, Validators.min(1)]],
      mpaaRating: ['', Validators.required],
      tagline: ['', Validators.required]
    });
  }

  movieForm: FormGroup
  createFlag = 0
  updateFlag = 0
  deleteFlag = 0
  searchFlag = 0
  notsFlag = 0
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
    genre: ''
  };

  movies = [this.movie];

  createMovie() {
    if (this.movieForm.valid) {
      this.http.post('/api/create', this.movieForm.value).subscribe(
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
}
