import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {HttpClient, HttpClientModule} from '@angular/common/http';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink,
    HttpClientModule
  ],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent {
  constructor(private http: HttpClient, private router: Router) {}

  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  isAdminRequest: boolean = false;

  register() {
    if (this.password !== this.confirmPassword) {
      alert("Passwords are different!");
      return;
    }

    const userData = {
      email: this.email,
      password: this.password,
    };

    this.http.post(`/api/users/register?isAdminRequest=${this.isAdminRequest}`, userData)
      .subscribe(
        (response) => {
          alert("Registration sucessful!");
          this.router.navigate(['/login']);
        },
        (error) => {
          if (error.status === 202) {
            alert("A request to register as an administrator has been created, please wait for approval.");
          } else {
            alert("Registration error:" + error.message);
          }
        }
      );
  }
}
