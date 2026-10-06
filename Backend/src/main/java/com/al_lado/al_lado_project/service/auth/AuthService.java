package com.al_lado.al_lado_project.service.auth;

import com.al_lado.al_lado_project.dto.auth.AuthRequestDTO;
import com.al_lado.al_lado_project.dto.auth.AuthResponseDTO;
import com.al_lado.al_lado_project.dto.auth.RegisterRequestDTO;
import com.al_lado.al_lado_project.dto.auth.ChangePasswordDTO;
import com.al_lado.al_lado_project.dto.auth.ForgotPasswordRequestDTO;
import com.al_lado.al_lado_project.dto.auth.ResetPasswordRequestDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO request);
    AuthResponseDTO register(RegisterRequestDTO request);
    void verifyEmail(String token);
    void changePassword(ChangePasswordDTO request);
    void verifyCurrentPassword(ChangePasswordDTO request);
    void forgotPassword(ForgotPasswordRequestDTO request);
    void resetPassword(ResetPasswordRequestDTO request);
    void logout(String bearerToken);
}