import { Component } from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import CryptoJS from 'crypto-js';
import {NgIf} from '@angular/common';
import {AuthGuard} from '../../auth.guard';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, HttpClientModule, NgIf],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  constructor(private http: HttpClient, private router: Router, private authGuard: AuthGuard) {}

  email: string = '';
  password: string = '';
  isAdminLogin: boolean = false;

  emailCriteria = {
    isEmail: false
  }

  passwordCriteria = {
    hasLowercase: false,
    hasUppercase: false,
    minLength: false,
    hasNumberOrSymbol: false
  };

  login() {
    const loginData = {
      email: this.email,
      password: this.password,
    };
    this.http.post<any>(`/api/users/login/${this.isAdminLogin}`, loginData, { observe: 'response' })
      .subscribe(
        (response: any) => {
          if (response.status === 200) {
            if (this.isAdminLogin && response.body?.message === "The administrator has successfully logged in") {
              sessionStorage.setItem('isAdmin', 'true');
            }
            sessionStorage.setItem('loggedInUser', 'true');
            sessionStorage.setItem('loggedInUserEmail', this.email);

            alert(response.body?.message || "Login successful");
            this.authGuard.markProgrammaticNavigation();
            this.router.navigate(['/home']);
          } else {
            alert(response.body?.message);
          }
        },
        (error) => {
          alert(error.error?.message);
        }
      );
  }

  checkEmailRequirements(): void {
    this.emailCriteria.isEmail = /^[a-zA-Z0-9._%+-]+@(gmail\.com|yandex\.ru|mail\.ru)$/.test(this.email);
  }

  isEmailValid(): boolean {
    return Object.values(this.emailCriteria).every(criterion => criterion);
  }

  checkPasswordRequirements(): void {
    this.passwordCriteria.hasLowercase = /[a-z]/.test(this.password);
    this.passwordCriteria.hasUppercase = /[A-Z]/.test(this.password);
    this.passwordCriteria.minLength = this.password.length >= 6;
    this.passwordCriteria.hasNumberOrSymbol = /[\d!@#\$%\^&\*\(\)_\+]/.test(this.password);
  }

  isPasswordValid(): boolean {
    return Object.values(this.passwordCriteria).every(criterion => criterion);
  }

  navigateToRegister(): void {
    this.authGuard.markProgrammaticNavigation();
    this.router.navigate(['/register']);
  }
}
