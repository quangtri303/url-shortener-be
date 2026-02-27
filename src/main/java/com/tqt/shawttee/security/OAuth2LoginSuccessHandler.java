package com.tqt.shawttee.security;

import com.tqt.shawttee.entity.Provider;
import com.tqt.shawttee.entity.User;
import com.tqt.shawttee.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Value("${FE_URL}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        //check google or GitHub
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = null;
        String providerId = null;
        Provider providerEnum = Provider.GOOGLE;

        if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            providerId = (String) attributes.get("sub");
        } else if ("github".equals(registrationId)) {
            email = (String) attributes.get("email");
            Integer id = (Integer) attributes.get("id");
            providerId = String.valueOf(id);
            providerEnum = Provider.GITHUB;

            // HANDLE NULL EMAIL:
            if (email == null) {
                String login = (String) attributes.get("login");
                email = login + "@github.placeholder";
            }
        }

        String finalEmail = email;
        Provider finalProvider = providerEnum;
        String finalProviderId = providerId;

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Create new user if not found
                    return userRepository.save(User.builder()
                            .email(finalEmail)
                            .provider(finalProvider)
                            .providerId(finalProviderId)
                            .role("USER")
                            .build());
                });
        String token = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole(), user.getCreatedAt().toString(), user.getProvider().name());

        this.setDefaultTargetUrl(frontendUrl + "/oauth2/redirect?token=" + token);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
