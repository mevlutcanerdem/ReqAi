-- 1. Sahipsiz olan eski test belgelerini (verileri) tamamen temizle
DELETE FROM documents;

-- 2. user_id sütununu ekle (IF NOT EXISTS ekliyoruz ki işlem yarıda kaldıysa hata vermesin)
ALTER TABLE documents ADD COLUMN IF NOT EXISTS user_id BIGINT;

-- 3. Artık tablo boş olduğu için içindeki null değerlerle çakışmayacak, güvenle zorunlu yapabiliriz
ALTER TABLE documents ALTER COLUMN user_id SET NOT NULL;