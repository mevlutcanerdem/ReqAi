import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { clearStoredToken, hasValidToken } from './utils/token.util';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
  standalone: true,
  imports: [RouterOutlet, RouterLink]
})
export class AppComponent {
  authenticated = hasValidToken();

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.authenticated = hasValidToken();
      });
  }

  logout(): void {
    clearStoredToken();
    this.authenticated = false;
    this.router.navigate(['/login']);
  }
}
