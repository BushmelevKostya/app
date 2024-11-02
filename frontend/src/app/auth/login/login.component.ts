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

  emailError: string | null = null;
  passwordError: string | null = null;

  login(form: NgForm) {
    this.resetErrors();
    const emailControl = form.controls['email'];
    const passwordControl = form.controls['password'];

    if (!this.validateEmail(this.email)) {
      this.emailError = "Invalid email format (use gmail, yandex, or mail)";
      emailControl.setErrors({'incorrect': true});
      emailControl.markAsTouched();
    }
    if (this.email === '') {
      this.emailError = 'Email can not be empty';
      emailControl.setErrors({'incorrect': true});
      emailControl.markAsTouched();
    }
    if (!this.validatePassword(this.password)) {
      this.passwordError = "Password must be at least 6 characters, no spaces, and contain letters or numbers";
      passwordControl.setErrors({'incorrect': true});
      passwordControl.markAsTouched();
    }
    if (this.password === '') {
      this.passwordError = 'Password can not be empty';
      passwordControl.setErrors({'incorrect': true});
      passwordControl.markAsTouched();
    }

    if (emailControl.invalid || passwordControl.invalid) {
      return;
    }

    const loginData = {
      email: this.email,
      password: this.password,
    };
    console.log(loginData)
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
          } else if (response.status === 202) {
            alert(response.body?.message);
          }
        },
        (error) => {
          alert("Error: " + error.message);
        }
      );
  }

  resetErrors() {
    this.emailError = '';
    this.passwordError = '';
  }

  hashPassword(password: string): string {
    const pepper = 'MXiJw7Hz';
    return CryptoJS.SHA256(password + pepper).toString(CryptoJS.enc.Hex);
  }

  validateEmail(email: string): boolean {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@(gmail\.com|yandex\.ru|mail\.ru)$/;
    return emailPattern.test(email);
  }

  validatePassword(password: string): boolean {
    const minLength = 6;
    if (!password || password.length < minLength || /\s/.test(password)) {
      return false;
    }
    const passwordPattern = /^[a-zA-Z0-9!@#\$%\^&\*\(\)_\+]+$/;
    return passwordPattern.test(password);
  }

  navigateToRegister(): void {
    this.authGuard.markProgrammaticNavigation();
    this.router.navigate(['/register']);
  }
}
