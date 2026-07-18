package com.reqai.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
    @NonNull   HttpServletRequest request,
    @NonNull   HttpServletResponse response,
    @NonNull   FilterChain filterChain
    ) throws ServletException, IOException {


        // isteğin başındaki header (authorization ) kısmını al
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // eğer başlık yoksa veya "bearer " ile başlamıyorsa bu adamda bileklik yok demektir sal gitsin
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        // bearer kısmını kesip at sadece tokeni al
        jwt = authHeader.substring(7);

        // tokenin içinden kullanıcı adını çıkar
        username = jwtService.extractUsername(jwt);

        // eğer kullanıcı adı var ve henüz sisteme giriş yapmamışsa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            // veritabanından bu adamı bul
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // token geçerli mi diye sor
            if (jwtService.isTokenValid(jwt,userDetails)){
                // geçerliyse bu adama güvenilir damgasını vur ve içeri al
                UsernamePasswordAuthenticationToken  auhtToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                auhtToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auhtToken);

            }
        }
        filterChain.doFilter(request,response);


    }
}
