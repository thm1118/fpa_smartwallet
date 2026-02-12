package com.fintech.smartwallet.controller;

import com.fintech.smartwallet.dto.JwtResponse;
import com.fintech.smartwallet.dto.LoginRequest;
import com.fintech.smartwallet.dto.RegisterRequest;
import com.fintech.smartwallet.dto.UserDTO;
import com.fintech.smartwallet.security.JwtTokenProvider;
import com.fintech.smartwallet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = userService.register(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        UserDTO user = userService.convertToDTO(
            ((com.fintech.smartwallet.security.UserPrincipal) authentication.getPrincipal()).getUser()
        );

        return ResponseEntity.ok(new JwtResponse(token, user));
    }
}
