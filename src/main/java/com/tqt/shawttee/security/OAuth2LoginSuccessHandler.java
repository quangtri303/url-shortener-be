package com.tqt.shawttee.security;

import com.tqt.shawttee.entity.Provider;
import com.tqt.shawttee.entity.User;
import com.tqt.shawttee.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Create new user if not found
                    return userRepository.save(User.builder()
                            .email(email)
                            .provider(Provider.GOOGLE)
                            .providerId(providerId)
                            .role("USER")
                            .build());
                });
        String token = jwtUtils.generateToken(user.getEmail());
        this.setDefaultTargetUrl(frontendUrl + "/oauth2/redirect?token=" + token);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
