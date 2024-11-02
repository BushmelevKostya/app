import { Injectable } from '@angular/core';
import { CanActivate, Router, NavigationEnd, RouterStateSnapshot } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  private lastNavigationProgrammatic: boolean = false;

  constructor(private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.lastNavigationProgrammatic = false; // Сбрасываем флаг после завершения навигации
      }
    });
  }

  canActivate(route: any, state: RouterStateSnapshot): boolean {
    if (!this.lastNavigationProgrammatic) {
      this.router.navigate(['/login']); // Перенаправляем на login, если навигация не программная
      return false;
    }
    return true;
  }

  public markProgrammaticNavigation(): void {
    this.lastNavigationProgrammatic = true;
  }
}
