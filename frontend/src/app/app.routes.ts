import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { HomeComponent } from './home/home.component';
import { RegistrationComponent } from './auth/registration/registration.component';
import { AuthGuard } from './auth.guard';
import {VisualizationComponent} from './visualization/visualization.component';

export const routes: Routes = [
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard] },
  { path: 'register', component: RegistrationComponent, canActivate: [AuthGuard] },
  { path: 'visualize', component: VisualizationComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'login', pathMatch: 'full' },
];
