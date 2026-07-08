package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.auth.JwtResponse;
import com.laptopstore.application.dto.auth.LoginRequest;
import com.laptopstore.application.dto.auth.SignupRequest;
import com.laptopstore.application.facade.AuthFacade;
import com.laptopstore.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authFacade.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authFacade.registerUser(signUpRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully!"));
    }
}
