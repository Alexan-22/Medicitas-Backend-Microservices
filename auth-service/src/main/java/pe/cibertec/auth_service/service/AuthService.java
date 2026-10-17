package pe.cibertec.auth_service.service;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.auth_service.dto.AuthResponse;
import pe.cibertec.auth_service.dto.LoginRequest;
import pe.cibertec.auth_service.dto.RegisterRequest;
import pe.cibertec.auth_service.entity.Usuario;
import pe.cibertec.auth_service.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        if (request == null || request.getUsername() == null
                || request.getUsername().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ingresa un usuario");
        }

        String username = request.getUsername().trim();
        String password = request.getPassword();

        if (password == null || password.isBlank() || password.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos 8 caracteres");
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La contraseña es demasiado larga");
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));

        // El registro público siempre crea pacientes.
        usuario.setRol("PACIENTE");

        usuarioRepository.save(usuario);
        return "Usuario registrado correctamente";
    }

    public AuthResponse login(LoginRequest request) {
        if (request == null || request.getUsername() == null
                || request.getUsername().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }

        Usuario usuario = usuarioRepository
                .findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));

        if (!passwordEncoder.matches(
                request.getPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }

        return new AuthResponse(
                "Login correcto",
                jwtService.generarToken(usuario),
                usuario.getUsername(),
                usuario.getRol()
        );
    }
}