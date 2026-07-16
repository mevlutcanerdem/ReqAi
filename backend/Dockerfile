# 1. Aşama: Kodları Derleme (Build)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Sadece pom.xml'i ve src klasörünü kopyalıyoruz
COPY pom.xml .
COPY src ./src

# Maven ile projeyi derleyip .jar dosyasını oluşturuyoruz (Testleri atlayarak süreci hızlandırıyoruz)
RUN mvn clean package -DskipTests

# 2. Aşama: Uygulamayı Çalıştırma (Run)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 1. aşamada oluşan .jar dosyasını bu yeni ve temiz kutuya kopyalıyoruz
COPY --from=build /app/target/*.jar app.jar

# Uygulamayı ayağa kaldırıyoruz
ENTRYPOINT ["java", "-jar", "app.jar"]