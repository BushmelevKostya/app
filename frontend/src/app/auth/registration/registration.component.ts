import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent {
  constructor( private router: Router ) {}
  register() {
    this.router.navigate(['/login'])
  }
}
