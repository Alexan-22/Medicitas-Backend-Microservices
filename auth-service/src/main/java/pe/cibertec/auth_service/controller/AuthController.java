package pe.cibertec.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import pe.cibertec.auth_service.dto.AuthResponse;
import pe.cibertec.auth_service.dto.LoginRequest;
import pe.cibertec.auth_service.dto.RegisterRequest;
import pe.cibertec.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request){

        String mensaje = authService.register(request);

        return ResponseEntity.ok(
                new AuthResponse(mensaje));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request){

        String mensaje = authService.login(request);

        return ResponseEntity.ok(
                new AuthResponse(mensaje));
    }
}
