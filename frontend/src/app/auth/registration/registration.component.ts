import {Component} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {NgClass, NgIf} from '@angular/common';
import {AuthGuard} from '../../auth.guard';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink,
    HttpClientModule,
    NgIf,
    NgClass
  ],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent {
  constructor(private http: HttpClient, private router: Router, private authGuard: AuthGuard) {
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
      };

      this.http.post(`/api/users/register?isAdminRequest=${this.isAdminRequest}`, userData)
        .subscribe(
          (response) => {
            alert("Registration sucessful!");
            this.authGuard.markProgrammaticNavigation()
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
    });
  }
  checkUniqueEmail(email: string) {
    return this.http.get<boolean>(`http://localhost:2580/api/users/check-email?email=${email}`);
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
