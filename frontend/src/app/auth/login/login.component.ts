import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  constructor( private router: Router ) {}

  home() {
    this.router.navigate(['/home'])
  }

  register() {
    this.router.navigate(['/register'])
  }
}
