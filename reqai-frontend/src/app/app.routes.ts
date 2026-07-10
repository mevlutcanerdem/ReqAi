import { Routes } from '@angular/router';

// DİKKAT: .component yazılarını sildik, sadece klasör/dosya adı kaldı!
import { UploadComponent } from './upload/upload';
import { HistoryComponent } from './history/history';
import { DetailComponent } from './detail/detail';

export const routes: Routes = [
  { path: '', redirectTo: '/upload', pathMatch: 'full' },
  { path: 'upload', component: UploadComponent },
  { path: 'history', component: HistoryComponent },
  { path: 'analysis/:id', component: DetailComponent }
];
