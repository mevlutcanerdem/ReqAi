import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { clearStoredToken, hasValidToken } from './utils/token.util';
import { TranslationService, SupportedLanguage } from './services/translation.service';
import { TranslatePipe } from './utils/translate.pipe';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
  standalone: true,
  imports: [RouterOutlet, RouterLink, TranslatePipe]
})
export class AppComponent {
  authenticated = hasValidToken();

  constructor(
    private router: Router,
    public translationService: TranslationService
  ) {
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

  switchLanguage(lang: SupportedLanguage) {
    this.translationService.setLanguage(lang);
  }
}
