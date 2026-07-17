-- Yanlış tiple (BIGINT) açılan sütunu siliyoruz
ALTER TABLE documents DROP COLUMN user_id;

-- Doğru tiple (UUID) zorunlu olarak yeniden ekliyoruz
ALTER TABLE documents ADD COLUMN user_id UUID NOT NULL;