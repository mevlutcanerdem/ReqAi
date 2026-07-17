package com.reqai.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

                // cors ayarları aynı kalsın
                .cors(Customizer.withDefaults())

                // hangi odalara kimler girebilir
                .authorizeHttpRequests(auth -> auth

                // sadece api/v1/auth ile başlayan odalar herkese açık  (login / register)
                                .requestMatchers("/api/v1/auth/**").permitAll()
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


}
