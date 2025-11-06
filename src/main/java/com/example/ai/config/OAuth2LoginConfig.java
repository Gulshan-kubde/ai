package com.example.ai.config;

import com.example.ai.model.User;
import com.example.ai.repository.UserRepository;
import com.example.ai.service.TempAuthCodeStore;
import com.example.ai.dto.response.AuthData;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class OAuth2LoginConfig implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final TempAuthCodeStore tempAuthCodeStore;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            response.sendRedirect("/login/error");
            return;
        }

        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        OAuth2AuthorizedClient client =
                authorizedClientService.loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(),
                        oauthToken.getName());

        String accessToken = (client != null && client.getAccessToken() != null)
                ? client.getAccessToken().getTokenValue()
                : null;

        String email = extractEmail(attributes, provider);
        if ("github".equalsIgnoreCase(provider) &&
                (email == null || email.isBlank() || "null".equalsIgnoreCase(email))) {
            email = fetchGithubEmail(accessToken);
        }

        String name = extractName(attributes, provider);
        String oauthId = extractId(attributes);

        // Create or update user
        String finalEmail = email;
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(finalEmail)
                        .fullName(name)
                        .role(User.Role.USER)
                        .oauthProvider(provider)
                        .oauthId(oauthId)
                        .verified(true)
                        .createdAt(LocalDateTime.now())
                        .build());
        userRepository.save(user);

        // Create JWT
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder()
                .setSubject(email)
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Generate temporary auth code
        String code = UUID.randomUUID().toString();
        tempAuthCodeStore.save(code, new AuthData(jwt, user));

        // Redirect only with short code, not JWT
        response.sendRedirect("http://localhost:3000/oauth2/callback?code=" + code);
    }

    private String extractEmail(Map<String, Object> attrs, String provider) {
        if ("google".equalsIgnoreCase(provider)) return (String) attrs.get("email");
        if ("github".equalsIgnoreCase(provider)) return (String) attrs.get("email");
        if ("microsoft".equalsIgnoreCase(provider)) return (String) attrs.get("mail");
        return "unknown@unknown.com";
    }

    private String extractName(Map<String, Object> attrs, String provider) {
        if ("google".equalsIgnoreCase(provider)) return (String) attrs.get("name");
        if ("github".equalsIgnoreCase(provider)) return (String) attrs.getOrDefault("name", attrs.get("login"));
        if ("microsoft".equalsIgnoreCase(provider)) return (String) attrs.get("displayName");
        return "Unknown";
    }

    private String extractId(Map<String, Object> attrs) {
        Object idObj = attrs.get("id");
        return (idObj != null) ? String.valueOf(idObj) : "";
    }

    private String fetchGithubEmail(String accessToken) {
        if (accessToken == null) return "unknown@github.com";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            if (response.getBody() != null) {
                for (Object obj : response.getBody()) {
                    Map<String, Object> emailInfo = (Map<String, Object>) obj;
                    if (Boolean.TRUE.equals(emailInfo.get("primary")) &&
                            Boolean.TRUE.equals(emailInfo.get("verified"))) {
                        return (String) emailInfo.get("email");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown@github.com";
    }
}
