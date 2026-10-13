package pe.cibertec.auth_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.auth_service.dto.*;
import pe.cibertec.auth_service.entity.Usuario;
import pe.cibertec.auth_service.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return "El usuario ya existe";
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());

        usuarioRepository.save(usuario);
        return "Usuario registrado correctamente";
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"
                ));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"
            );
        }

        return new AuthResponse(
                "Login correcto",
                jwtService.generarToken(usuario),
                usuario.getUsername(),
                usuario.getRol()
        );
    }
}
