import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Canlı backend sunucunun adresi
  private apiUrl = 'https://reqaiweb.onrender.com/api/v1/auth';

  // HttpClient'ı inject ediyoruz ki backend'e istek atabilelim
  constructor(private http: HttpClient) { }

  // 1. Kayıt Olma Metodu
  register(userData: any): Observable<any> {
    // Backend'in /register ucuna kullanıcının girdiği verileri POST ediyoruz
    return this.http.post(`${this.apiUrl}/register`, userData);
  }

  // 2. Giriş Yapma Metodu
  login(userData: any): Observable<any> {
    // Backend'in /authenticate (veya login) ucuna verileri POST ediyoruz
    return this.http.post(`${this.apiUrl}/authenticate`, userData);
  }
}
