#  bana içinde java 21 olan bir linux ver
FROM eclipse-temurin:21-jre-alpine
# kendi projemizi kutunun içine kopyalıyoruz
COPY target/*.jar app.jar
# kutu çalıştırıldığında içeride hangi komutun çalışacağını söylüyoruz
ENTRYPOINT ["java","-jar","/app.jar"]



