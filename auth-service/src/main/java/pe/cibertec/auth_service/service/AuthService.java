package pe.cibertec.auth_service.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.cibertec.auth_service.dto.LoginRequest;
import pe.cibertec.auth_service.dto.RegisterRequest;
import pe.cibertec.auth_service.entity.Usuario;
import pe.cibertec.auth_service.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    

    public String register(RegisterRequest request) {

        Optional<Usuario> usuarioExistente =
                usuarioRepository.findByUsername(request.getUsername());

        if(usuarioExistente.isPresent()) {
            return "El usuario ya existe";
        }

        Usuario usuario = new Usuario();

        usuario.setUsername(request.getUsername());

        usuario.setPassword(
                passwordEncoder.encode(request.getPassword()));

        usuario.setRol(request.getRol());

        usuarioRepository.save(usuario);

        return "Usuario registrado correctamente";
    }

    public String login(LoginRequest request) {

        Optional<Usuario> usuario =
                usuarioRepository.findByUsername(request.getUsername());

        if(usuario.isEmpty()) {
            return "Usuario no encontrado";
        }

        boolean passwordCorrecto =
                passwordEncoder.matches(
                        request.getPassword(),
                        usuario.get().getPassword());

        if(passwordCorrecto) {
            return "Login correcto";
        }

        return "Contraseña incorrecta";
    }
}
