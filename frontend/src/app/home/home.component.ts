import { Component } from '@angular/core';
import { Router } from '@angular/router'
import {FormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  title = 'Лабораторная работа'
  searchId: number | null = null;

  movie = {
    id: 1,
    name: '',
    coordinates: { x: 0, y: 0 },
    oscarsCount: 0,
    budget: 0,
    totalBoxOffice: 0,
    mpaaRating: '',
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
  constructor( private router: Router ) {}

  navigateToLogin() {
    this.router.navigate(['/login'])
  }

  createMovie() {
    //TODO
  }

  searchMovieById() {
    //TODO
  }

  updateMovie(id: number) {
    //TODO
  }

  deleteMovie(id: number) {
    //TODO
  }
}
