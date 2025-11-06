package com.example.ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI aiInterviewOpenAPI() {
        return new OpenAPI()
                // 🔐 JWT Auth Support
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token. Example: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")))
                // 📋 General API Info
                .info(new Info()
                        .title("AI-Driven Interview Automation System API")
                        .description("""
                            This API powers the AI-driven Interview Automation Platform.
                            It handles authentication, recruiter job management, candidate uploads, 
                            and AI interview scheduling.
                            """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("AI Platform Team")
                                .email("support@ai-interview.com")
                                .url("https://ai-interview.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
