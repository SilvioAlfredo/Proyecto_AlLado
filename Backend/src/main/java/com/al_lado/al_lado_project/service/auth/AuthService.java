package com.al_lado.al_lado_project.service.auth;

import com.al_lado.al_lado_project.dto.auth.AuthRequestDTO;
import com.al_lado.al_lado_project.dto.auth.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO request);
}