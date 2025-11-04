package com.example.ai.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class OAuth2StateAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public OAuth2StateAuthorizationRequestResolver(ClientRegistrationRepository repo) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest original = defaultResolver.resolve(request);
        return customize(original, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest original = defaultResolver.resolve(request, clientRegistrationId);
        return customize(original, request);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest original, HttpServletRequest request) {
        if (original == null) return null;

        String role = request.getParameter("role");
        if (role == null) role = "USER";

        // Embed role inside state (safe, short-lived)
        String newState = original.getState() + "::" + role.toUpperCase();

        return OAuth2AuthorizationRequest.from(original)
                .state(newState)
                .build();
    }
}
