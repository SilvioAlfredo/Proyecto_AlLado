package com.al_lado.al_lado_project.controller.auth;

import com.al_lado.al_lado_project.dto.auth.AuthRequestDTO;
import com.al_lado.al_lado_project.dto.auth.AuthResponseDTO;
import com.al_lado.al_lado_project.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}