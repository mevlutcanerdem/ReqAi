import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  // Angular'ın yönlendirme servisini (Router) içeri alıyoruz
  const router = inject(Router);

  // Tarayıcının hafızasına bakıyoruz, giriş yaparken koyduğumuz token orada mı?
  const token = localStorage.getItem('reqai_token');

  if (token) {
    // Token varsa: "Geçebilirsin, kapı açık!"
    return true;
  } else {
    // Token yoksa: "Biletin yok, hemen Login sayfasına dön!"
    router.navigate(['/login']);
    return false;
  }
};
