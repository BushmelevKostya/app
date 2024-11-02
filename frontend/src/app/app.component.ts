import { Component } from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import { AuthGuard } from './auth.guard';
import {ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  constructor(private router: Router, private authGuard: AuthGuard) {
  }

  ngOnInit() {
    this.authGuard.markProgrammaticNavigation();
    this.router.navigate(['/login']);
  }
}
