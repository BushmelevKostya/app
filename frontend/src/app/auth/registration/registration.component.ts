import {Component} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {NgClass, NgIf} from '@angular/common';
import {AuthGuard} from '../../auth.guard';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink,
    NgIf,
    NgClass
  ],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent {
  constructor(
    private http: HttpClient,
    private router: Router,
    private authGuard: AuthGuard,
    private authService: AuthService
  ) {
  }

  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  isAdminRequest: boolean = false;

  emailCriteria = {
    isEmail: false
  }

  passwordCriteria = {
    hasLowercase: false,
    hasUppercase: false,
    minLength: false,
    hasNumberOrSymbol: false
  };

  passwordRepeatCriteria = {
    isEquals: false
  }

  register() {
    this.checkUniqueEmail(this.email).subscribe((isUnique: boolean) => {
      if (!isUnique) {
        alert('This email already exist');
        return;
      }

      const userData = {
        email: this.email,
        password: this.password,
        isAdminRequest: this.isAdminRequest
      };

      this.http.post<any>(`/api/auth/register`, userData)
        .subscribe(
          (response) => {
            if (response?.token) {
              // Auto-login after registration
              this.authService.setToken(response.token);
              sessionStorage.setItem('loggedInUser', 'true');
              sessionStorage.setItem('loggedInUserEmail', response.email || this.email);
              sessionStorage.setItem('isAdmin', response.isAdmin ? 'true' : 'false');
              sessionStorage.setItem('isApprovedAdmin', response.isApprovedAdmin ? 'true' : 'false');
              
              alert("Registration successful!");
              this.authGuard.markProgrammaticNavigation();
              this.router.navigate(['/home']);
            } else {
              alert("Registration successful! Please login.");
              this.authGuard.markProgrammaticNavigation();
              this.router.navigate(['/login']);
            }
          },
          (error) => {
            const errorMessage = error.error?.message || 'Unknown error occurred';
            alert(`Error: ${errorMessage}`);
            console.error('Full error details:', error); // Выводит всю информацию об ошибке в консоль
          }
        );
    });
  }
  checkUniqueEmail(email: string) {
    return this.http.get<boolean>(`/api/users/check-email?email=${email}`);
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

  checkRepeatPasswordRequirements(): void {
    this.passwordRepeatCriteria.isEquals = this.password == this.confirmPassword;
  }

  isRepeatPasswordValid(): boolean {
    return Object.values(this.passwordRepeatCriteria).every(criterion => criterion);
  }

  navigateToLogin(): void {
    this.authGuard.markProgrammaticNavigation();
    this.router.navigate(['/login']);
  }
}
