import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {HttpClient, HttpClientModule} from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  constructor( private http: HttpClient, private router: Router ) {}

  email: string = '';
  password: string = '';
  isAdminLogin: boolean = false;

  login() {
    const loginData = {
      email: this.email,
      password: this.password,
      isAdmin: this.isAdminLogin
    };

    this.http.post(`/api/users/login`, loginData)
      .subscribe(
        (response: any) => {
          alert(response.message)
          if (this.isAdminLogin && response === "Администратор успешно вошел") {
            sessionStorage.setItem('isAdmin', 'true');
          }
          this.router.navigate(['/home']);
        },
        (error) => {
          alert("Ошибка входа: " + error.message)
        }
      );
  }

  register() {
    this.router.navigate(['/register'])
  }
}
