package com.laptopstore.application.facade;

import com.laptopstore.application.dto.auth.JwtResponse;
import com.laptopstore.application.dto.auth.LoginRequest;
import com.laptopstore.application.dto.auth.SignupRequest;
import com.laptopstore.application.security.jwt.JwtUtils;
import com.laptopstore.application.security.services.UserDetailsImpl;
import com.laptopstore.business.service.UserService;
import com.laptopstore.common.constants.SecurityConstants;
import com.laptopstore.data.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userService.getUserById(userDetails.getId());

        return JwtResponse.builder()
                .token(jwt)
                // Refresh token logic would go here, omitting for simplicity unless requested
                .refreshToken("dummy-refresh-token") 
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .roles(roles)
                .build();
    }

    public void registerUser(SignupRequest signUpRequest) {
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .fullName(signUpRequest.getFullName())
                .enabled(true)
                .build();

        userService.createUser(user, SecurityConstants.ROLE_USER);
    }
}
