# --- 1. AŞAMA: BUILD (Projenin Derlenmesi) ---
# Node.js'in hafif bir sürümünü kullanarak kodu derliyoruz
FROM node:22-alpine AS build
WORKDIR /app

# Önce sadece paket bilgilerini kopyalayıp indiriyoruz (Cache mekanizması için önemli)
COPY package*.json ./
RUN npm install

# Tüm proje kodlarını kopyalayıp 'Production' (Canlı ortam) versiyonunu üretiyoruz
COPY . .
RUN npm run build --configuration=production

# --- 2. AŞAMA: SERVE (Nginx ile Sunma) ---
# Derlenen kodları yayınlamak için çok hafif bir web sunucusu olan Nginx kullanıyoruz
FROM nginx:alpine

# Nginx'in varsayılan sayfasını siliyoruz
RUN rm -rf /usr/share/nginx/html/*

# 1. aşamada üretilen o saf HTML/CSS/JS dosyalarını Nginx'in içine atıyoruz
# Not: Angular son sürümlerinde çıktıları 'browser' klasörüne atar.
COPY --from=build /app/dist/reqai-frontend/browser /usr/share/nginx/html

# Nginx'in dünyayla iletişim kuracağı portu açıyoruz
EXPOSE 80

# Nginx sunucusunu başlatıyoruz
CMD ["nginx", "-g", "daemon off;"]
