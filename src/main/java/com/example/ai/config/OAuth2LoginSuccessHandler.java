package com.example.ai.config;


import com.example.ai.model.User;
import com.example.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
logger.info("kfdfdfdh "+email+"  name"+name);
        // Save or update user
        userRepository.findByEmail(email).ifPresentOrElse(
                existing -> {
//                    existing.setFullName(name);
//                    //existing.setPictureUrl(picture);
//                    userRepository.save(existing);
                },
                () -> {
                    User newUser = User.builder()
                            .fullName(name)
                            //.name(name)
                            .email(email)
                            //.provider("GOOGLE")
                           // .pictureUrl(picture)
                            .build();
                    userRepository.save(newUser);
                }
        );

        // redirect back to React frontend
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000");
    }
}