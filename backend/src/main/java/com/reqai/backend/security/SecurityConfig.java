package com.reqai.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                // 1 . CSRF korumasını kapatıyoruz çünkü jwt kullanıyoruz
                .csrf(csrf -> csrf.disable())

                // cors ayarları - açıkça bağlıyoruz
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // hangi odalara kimler girebilir
                .authorizeHttpRequests(auth -> auth

                        // optionsa izin ver
                                .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                // sadece api/v1/auth ile başlayan odalar herkese açık  (login / register)
                                .requestMatchers("/api/v1/auth/**").permitAll()
                        // Spring'in hata sayfasına da erişime izin ver (aksi halde controller hatası 403 olarak maskelenir)
                                .requestMatchers("/error").permitAll()
                        // geri kalan tüm isteklere token şart (belge yükleme / geçmiş görüntüleme)
                                .anyRequest().authenticated()

                )

                // session tutmuyoruz her istekte token konrtol edilecek  -stateless
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // bizim sağlayıcımızı tanıtıyoruz
                .authenticationProvider(authenticationProvider)

                // jwtFilter ı springin standart güvenliğinin önüne koyuyoruz
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        // JWT Authorization header kullanıldığı için cookie/credentials gerekmez
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
