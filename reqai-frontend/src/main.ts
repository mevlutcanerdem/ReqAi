import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app'; // Burada 'App' yazıyordu, 'AppComponent' olarak düzelttik

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
