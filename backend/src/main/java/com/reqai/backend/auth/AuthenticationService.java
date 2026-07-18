package com.reqai.backend.auth;

import com.reqai.backend.entity.User;
import com.reqai.backend.repository.UserRepository;
import com.reqai.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

// Bu sınıf, kullanıcının şifresini kriptolayıp veritabanına kaydetme
// ve giriş yaparken şifresini doğrulayıp Token üretme işini yapacak.
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // register process
    public AuthenticationResponse register(RegisterRequest request){
        User user = new User();
        user.setUsername(request.username());

        // şifreyi bycrpt ile şifreliyoruz
        user.setPassword(passwordEncoder.encode(request.password()));

        // Kalıcı API token üret (reqai_ ön ekiyle)
        String apiToken = "reqai_" + UUID.randomUUID().toString().replace("-", "");
        user.setToken(apiToken);

        // database e kaydet
        userRepository.save(user);

        // kayıt olan kullanıcıya kalıcı API token'ını ver
        return new AuthenticationResponse(apiToken);
    }

    // login process
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        // springsecurity bizim yerimize şifre eşleşiyor mu diye bakar
        authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(),request.password())
        );

        // hata fırlatmazsa şifre doğrudur kullanıcıyı veri tabanından bul
        User user = userRepository.findByUsername(request.username()).orElseThrow();

        // Eğer kullanıcının henüz kalıcı token'ı yoksa üret ve kaydet
        if (user.getToken() == null || user.getToken().isBlank()) {
            String apiToken = "reqai_" + UUID.randomUUID().toString().replace("-", "");
            user.setToken(apiToken);
            userRepository.save(user);
        }

        // kullanıcının kalıcı API token'ını ver
        return new AuthenticationResponse(user.getToken());
    }

}

