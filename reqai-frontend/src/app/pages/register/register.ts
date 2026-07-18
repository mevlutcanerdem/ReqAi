import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  // Artık backend ile tam uyumlu!
  registerData = {
    username: '',
    password: ''
  };

  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.register(this.registerData).subscribe({
      next: (response: any) => {
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        this.errorMessage = 'Kayıt işlemi başarısız oldu. Kullanıcı adı alınmış olabilir.';
        this.isLoading = false;
      }
    });
  }
}
