import { Routes } from '@angular/router';
// import { HomeComponent } from './home/home.component';
import { LoginComponent } from './auth/login/login.component';
// import { RegistrationComponent } from './auth/registration/registration.component';

export const routes: Routes = [
  // { path: '', component: HomeComponent }, // Главная страница
  { path: 'login', component: LoginComponent }, // Страница авторизации
  // { path: 'register', component: RegistrationComponent }, // Страница регистрации
  { path: '**', redirectTo: '', pathMatch: 'full' } // Перенаправление на главную, если путь не найден
];
