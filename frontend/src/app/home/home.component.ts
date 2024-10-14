import { Component } from '@angular/core';
import { Router } from '@angular/router'
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  createFlag = 0
  updateFlag = 0
  deleteFlag = 0
  searchFlag = 0
  title = 'Лабораторная работа'
  searchId: number | null = null;

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
  constructor( private router: Router ) {}

  login() {
    this.router.navigate(['/login'])
  }

  home() {
    this.router.navigate(['/home'])
  }

  createMovie() {
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
}
