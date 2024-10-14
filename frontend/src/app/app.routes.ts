import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import {HomeComponent} from './home/home.component';
// import { RegistrationComponent } from './auth/registration/registration.component';

export const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  // { path: 'register', component: RegistrationComponent },
  { path: '**', redirectTo: '/login', pathMatch: 'full' }
];
