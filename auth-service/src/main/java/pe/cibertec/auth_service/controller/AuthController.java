package pe.cibertec.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.auth_service.dto.*;
import pe.cibertec.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                new AuthResponse(authService.register(request))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<AuthResponse> manejarError(
            ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(new AuthResponse(
                        exception.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()
                                ? "Credenciales incorrectas"
                                : exception.getReason()
                ));
    }
}