package com.reqai.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Bu bizim kulübün gizli anahtarı. Bilekliklerin sahte olup olmadığını bununla anlıyoruz.
    // Normalde bu .env dosyasında saklanır ama şimdilik burada tutabiliriz. (En az 256-bit olmalı)
    private static final String SECRET_KEY = "ReqAiMevlutCanErdemSuperSecretKeyReqAiMevlutCanErdemSuperSecretKey";

    // 1. Gelen Token'dan kullanıcının adını okuyan metod
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Kullanıcı başarılı giriş yaptığında ona yeni Token üreten metod
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 saat geçerli
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. Gelen Token'ın bizim kullanıcıya mı ait olduğunu ve süresini kontrol eden metod
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}