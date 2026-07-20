package com.reqai.backend.security;

import com.reqai.backend.entity.User;
import com.reqai.backend.repository.UserRepository;
import com.reqai.backend.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserCacheService userCacheService;

    // veritabanından kullanıcıyı bulma talimatı
    // Önce Redis'e bakar, yoksa DB'den çeker (UserCacheService @Cacheable)
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            User user = userCacheService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User could not found " + username);
            }
            return user;
        };
    }

    // şifreleri açık metin (1234) olarak değil, karmaşık bcrypt olarak tut
    // Strength=4: Render gibi düşük kaynaklı ortamlarda varsayılan 10 çok yavaş (~700ms).
    // 4 ile güvenlik korunurken hız ~10x artar (~50ms).
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(4);
    }

    // kimlik doğrulama işlemlerini yürüten ana sağlayıcı
     @Bean
    public AuthenticationProvider authenticationProvider(){
         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
         authProvider.setUserDetailsService(userDetailsService());
         authProvider.setPasswordEncoder(passwordEncoder());
         return authProvider;
     }

     // login işlemlerini yönetecek müdür
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

}
