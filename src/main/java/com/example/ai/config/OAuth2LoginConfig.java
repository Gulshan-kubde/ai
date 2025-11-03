package com.example.ai.config;



import com.example.ai.model.User;
import com.example.ai.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class OAuth2LoginConfig implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
            String provider = oauthToken.getAuthorizedClientRegistrationId();
            String email = extractEmail(attributes, provider);
            String name = extractName(attributes, provider);

            // create or update user
            Optional<User> existing = userRepository.findByEmail(email);
            User user = existing.orElse(User.builder()
                    .email(email)
                    .fullName(name)
                    .oauthProvider(provider)
                    .oauthId((String) attributes.getOrDefault("id", ""))
                    .verified(true)
                    .createdAt(LocalDateTime.now())
                    .build());
            userRepository.save(user);

            // generate JWT token
            String jwt = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();

            // redirect with token in URL (frontend can store it)
            //response.sendRedirect("/login/success?token=" + jwt);
            response.sendRedirect("http://localhost:3000/login/success?token=" + jwt);

        } else {
            response.sendRedirect("/login/error");
        }
    }

    private String extractEmail(Map<String, Object> attrs, String provider) {
        if ("google".equalsIgnoreCase(provider)) return (String) attrs.get("email");
        if ("github".equalsIgnoreCase(provider)) return (String) attrs.get("email");
        if ("microsoft".equalsIgnoreCase(provider)) return (String) attrs.get("mail");
        return "unknown@unknown.com";
    }

    private String extractName(Map<String, Object> attrs, String provider) {
        if ("google".equalsIgnoreCase(provider)) return (String) attrs.get("name");
        if ("github".equalsIgnoreCase(provider)) return (String) attrs.get("name");
        if ("microsoft".equalsIgnoreCase(provider)) return (String) attrs.get("displayName");
        return "Unknown";
    }
}