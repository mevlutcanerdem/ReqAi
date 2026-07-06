package com.reqai.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                .title("ReqAI - AI-Powered Requirement Decomposition Platform API")
                .version("1.0.0")
                .description("This Api manages requirement document uploads and orchestrates" +
                        " the AI- powered decomposition process for generating tasks and test scenarios."));
    }
}
