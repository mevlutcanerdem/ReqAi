package com.reqai.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // this code just give access to APIs which we determine the frontend addresses
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**") // for all endpoints
                .allowedOrigins(
                        "http://localhost:4200", // angular
                        "http://localhost" // for docker nginx(port 80 )
                )
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS") // Allowed http operations
                .allowedHeaders("*") // permit all headers ex; formdata
                .allowCredentials(true);// if we use token etc. in future
    }
}
