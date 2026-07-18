import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  loginData = { username: '', password: '' };
  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.loginData).subscribe({
      next: (response: any) => {
        // 1. Gelen token'ı Local Storage'a kaydet (Interceptor buradan alacak)
        localStorage.setItem('reqai_token', response.token);

        // 2. Başarılı girişte ana sayfaya veya analiz sayfasına yönlendir
        this.router.navigate(['/']);
      },
      error: (err: any) => {
        // Hata durumunda kullanıcıya şık bir mesaj göster
        this.errorMessage = 'Giriş başarısız. Kullanıcı adı veya şifrenizi kontrol edin.';
        this.isLoading = false;
      }
    });
  }
}
