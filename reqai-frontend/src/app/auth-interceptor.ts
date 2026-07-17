import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Tarayıcının hafızasına az önce çaktığımız token'ı al
  const token = localStorage.getItem('reqai_token');

  // 2. Eğer token varsa, giden isteği klonla ve içine Authorization başlığını ekle
  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    // 3. Modifiye edilmiş isteği backend'e yolla
    return next(clonedRequest);
  }

  // Token yoksa isteği hiç ellemeden yolla (zaten 403 yiyecektir)
  return next(req);
};
