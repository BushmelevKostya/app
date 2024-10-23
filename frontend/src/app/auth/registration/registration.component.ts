import {Component} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {NgClass, NgIf} from '@angular/common';
import CryptoJS from 'crypto-js';

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
  constructor(private http: HttpClient, private router: Router) {
  }

  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  isAdminRequest: boolean = false;
  emailError: string = '';
  passwordError: string = '';
  passwordConfirmError: string = '';

  register(form: NgForm) {
    this.resetErrors();
    const emailControl = form.controls['email'];
    const passwordControl = form.controls['password'];
    const confirmPasswordControl = form.controls['passwordConfirm'];

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
    if (!this.confirmPassword) {
      this.passwordConfirmError = 'Password can not be empty';
      confirmPasswordControl.setErrors({'incorrect': true});
      confirmPasswordControl.markAsTouched();
    }
    if (this.password !== this.confirmPassword) {
      this.passwordConfirmError = 'Password are different';
      confirmPasswordControl.setErrors({'incorrect': true});
      confirmPasswordControl.markAsTouched();
    }

    this.checkUniqueEmail(this.email).subscribe((isUnique: boolean) => {
      if (!isUnique) {
        this.emailError = 'This email already exist';
        emailControl.setErrors({'incorrect': true});
        emailControl.markAsTouched();
      }

      if (emailControl.invalid || passwordControl.invalid || confirmPasswordControl.invalid) {
        return;
      }

      const hashedPassword = this.hashPassword(this.password);
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
    });
  }

  resetErrors() {
    this.emailError = '';
    this.passwordError = '';
    this.passwordConfirmError = '';
  }

  checkUniqueEmail(email: string) {
    return this.http.get<boolean>(`http://localhost:2580/api/users/check-email?email=${email}`);
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
}
