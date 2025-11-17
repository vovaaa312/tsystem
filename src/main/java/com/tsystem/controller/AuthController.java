package com.tsystem.controller;

import com.tsystem.exception.UnauthorizedException;
import com.tsystem.model.dto.*;
import com.tsystem.model.dto.request.LoginRequest;
import com.tsystem.model.dto.request.RegisterRequest;
import com.tsystem.model.dto.response.TokenResponse;
import com.tsystem.model.user.User;
import com.tsystem.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})

public class AuthController {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        ResponseEntity entity = ResponseEntity.ok(authService.register(request));

        logger.info("user registered: "+entity.toString());
        return entity;

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(
            @RequestBody LoginRequest loginRequest
    ) {

        ResponseEntity<TokenResponse> entity = ResponseEntity.ok(authService.authenticate(loginRequest));
        logger.info("login detected: "+entity.toString());
        return entity;
    }

    @PostMapping("/request-password-reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void requestReset(@RequestBody RequestPasswordReset req) {
        authService.requestPasswordReset(req);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@RequestBody ResetPassword req) {
        authService.resetPassword(req);
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody ChangePassword req,
                               @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) throw new UnauthorizedException("Unauthorized");
        authService.changePassword(req, principal.getUsername());
    }

     @GetMapping("/get-role")
     @SecurityRequirement(name = "bearerAuth")
//     @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> getRole(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(principal.getRole());
     }


}
