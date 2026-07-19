package com.reqai.backend.security;

import com.reqai.backend.entity.User;
import com.reqai.backend.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(
    @NonNull   HttpServletRequest request,
    @NonNull   HttpServletResponse response,
    @NonNull   FilterChain filterChain
    ) throws ServletException, IOException {


        // isteğin başındaki header (authorization ) kısmını al
        String authHeader = request.getHeader("Authorization");

        // EventSource Authorization header gönderemediği için SSE isteklerinde query param kullanılır
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            String queryToken = request.getParameter("token");
            if (queryToken != null && !queryToken.isBlank()) {
                authHeader = "Bearer " + queryToken.trim();
            }
        }

        // eğer başlık yoksa veya "bearer " ile başlamıyorsa bu adamda bileklik yok demektir sal gitsin
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        // ── KALICI API TOKEN KONTROLÜ (reqai_ ile başlıyorsa) ──
        if (token.startsWith("reqai_")) {
            System.out.println("[AUTH-FILTER] reqai_ token algılandı. URI: " + request.getRequestURI());
            System.out.println("[AUTH-FILTER] Token: " + token.substring(0, Math.min(token.length(), 20)) + "...");
            try {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    Optional<User> optionalUser = userRepository.findByToken(token);
                    System.out.println("[AUTH-FILTER] Token DB araması sonucu: " + (optionalUser.isPresent() ? "BULUNDU" : "BULUNAMADI"));
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("[AUTH-FILTER] ✅ Kimlik doğrulama BAŞARILI: " + user.getUsername());
                    } else {
                        System.out.println("[AUTH-FILTER] ❌ Token veritabanında bulunamadı!");
                    }
                } else {
                    System.out.println("[AUTH-FILTER] Zaten kimliği doğrulanmış: " + SecurityContextHolder.getContext().getAuthentication().getName());
                }
            } catch (Exception ex) {
                System.out.println("[AUTH-FILTER] ❌ Token doğrulama HATASI: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                ex.printStackTrace();
                SecurityContextHolder.clearContext();
            }
            System.out.println("[AUTH-FILTER] Son auth durumu: " + (SecurityContextHolder.getContext().getAuthentication() != null ? "AUTHENTICATED" : "ANONYMOUS"));
            filterChain.doFilter(request, response);
            return;
        }

        // ── LEGACY JWT TOKEN KONTROLÜ ──
        try {
            final String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);


    }
}
