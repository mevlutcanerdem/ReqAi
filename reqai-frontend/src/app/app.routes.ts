import { Routes } from '@angular/router';

// DİKKAT: .component yazılarını sildik, sadece klasör/dosya adı kaldı!
import { UploadComponent } from './upload/upload';
import { HistoryComponent } from './history/history';
import { DetailComponent } from './detail/detail';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register'; // Kayıt sayfasını ekledik
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  // 1. AÇIK ROTALAR (Herkes Girebilir)
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // 2. KORUMALI ROTALAR (Sadece Token'ı Olanlar Girebilir)
  { path: 'upload', component: UploadComponent, canActivate: [authGuard] },
  { path: 'history', component: HistoryComponent, canActivate: [authGuard] },
  { path: 'analysis/:id', component: DetailComponent, canActivate: [authGuard] },

  // 3. YÖNLENDİRMELER (Sıralama çok önemli)
  // Siteye ilk girildiğinde upload'a gitmeyi dener.
  // Token varsa içeri girer, yoksa guard onu anında login'e şutlar.
  { path: '', redirectTo: 'upload', pathMatch: 'full' },

  // DİKKAT: ** (Wildcard) ZORUNLU OLARAK EN SONDA OLMALIDIR!
  // Saçma sapan bir URL girilirse yine anasayfaya (upload'a) yönlendirir.
  { path: '**', redirectTo: 'upload' }
];
