import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import CryptoJS from 'crypto-js';


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
      password: this.hashPassword(this.password),
      isAdmin: this.isAdminLogin
    };

    this.http.post<any>(`/api/users/login`, loginData, {observe: 'response'})
      .subscribe(
        (response: any) => {
          if (response.status === 200) {
            if (this.isAdminLogin && response.body?.message === "The administrator has successfully logged in") {
              sessionStorage.setItem('isAdmin', 'true');
            }
            sessionStorage.setItem('loggedInUser', 'true');
            sessionStorage.setItem('loggedInUserEmail', this.email);

            alert(response.body?.message || "Login successful");
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

  hashPassword(password: string): string {
    const pepper = 'MXiJw7Hz';
    return CryptoJS.SHA256(password + pepper).toString(CryptoJS.enc.Hex);
  }
  register() {
    this.router.navigate(['/register'])
  }
}
