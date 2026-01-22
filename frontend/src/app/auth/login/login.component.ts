import { Component } from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import CryptoJS from 'crypto-js';
import {NgIf} from '@angular/common';
import {AuthGuard} from '../../auth.guard';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, NgIf],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  constructor(
    private http: HttpClient,
    private router: Router,
    private authGuard: AuthGuard,
    private authService: AuthService
  ) {}

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
    this.http.post<any>(`/api/auth/login?isAdminLogin=${this.isAdminLogin}`, loginData, { observe: 'response' })
      .subscribe(
        (response: any) => {
          if (response.status === 200 && response.body?.token) {
            // Save JWT token
            this.authService.setToken(response.body.token);
            
            // Save user info
            sessionStorage.setItem('loggedInUser', 'true');
            sessionStorage.setItem('loggedInUserEmail', response.body.email || this.email);
            sessionStorage.setItem('isAdmin', response.body.isAdmin ? 'true' : 'false');
            sessionStorage.setItem('isApprovedAdmin', response.body.isApprovedAdmin ? 'true' : 'false');

            alert("Login successful");
            this.authGuard.markProgrammaticNavigation();
            this.router.navigate(['/home']);
          } else {
            alert(response.body?.message || 'Login failed');
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
