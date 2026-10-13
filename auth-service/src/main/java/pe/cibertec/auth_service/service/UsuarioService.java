package pe.cibertec.auth_service.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.auth_service.dto.UsuarioRequest;
import pe.cibertec.auth_service.dto.UsuarioResponse;
import pe.cibertec.auth_service.entity.Usuario;
import pe.cibertec.auth_service.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Set<String> ROLES = Set.of(
            "ADMIN", "RECEPCIONISTA", "MEDICO", "ENFERMERA", "PACIENTE");

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponse> listar() {
        return repository.findAll().stream()
                .map(this::respuesta)
                .toList();
    }

    public UsuarioResponse buscar(Long id) {
        return respuesta(obtener(id));
    }

    @Transactional
    public UsuarioResponse guardar(UsuarioRequest request) {
        validar(request);

        String username = request.getUsername().trim();
        validarUsernameDisponible(username, null);
        validarPassword(request.getPassword());

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());

        return respuesta(repository.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        validar(request);

        Usuario usuario = obtener(id);
        String username = request.getUsername().trim();
        validarUsernameDisponible(username, id);

        usuario.setUsername(username);
        usuario.setRol(request.getRol());

        // Conserva la contraseña actual.
        return respuesta(repository.save(usuario));
    }

    private Usuario obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private void validar(UsuarioRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        }

        if (request.getRol() == null || !ROLES.contains(request.getRol())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El rol no es válido");
        }
    }

    private void validarUsernameDisponible(String username, Long excluirId) {
        repository.findByUsername(username).ifPresent(usuario -> {
            if (!usuario.getId().equals(excluirId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "El usuario ya existe");
            }
        });
    }

    private void validarPassword(String password) {
        if (password == null || password.isBlank() || password.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos 8 caracteres");
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña no debe superar 72 bytes");
        }
    }

    private UsuarioResponse respuesta(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(), usuario.getUsername(), usuario.getRol());
    }

    @Transactional
    public void eliminar(Long id, Long usuarioActualId) {
        if (id.equals(usuarioActualId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No puedes eliminar tu propia cuenta");
        }

        Usuario usuario = obtener(id);

        // Conserva las cuentas de administrador durante esta etapa.
        if ("ADMIN".equals(usuario.getRol())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se permite eliminar una cuenta de administrador");
        }

        repository.delete(usuario);
    }
    @Transactional
    public void cambiarPassword(Long id, String password) {
        validarPassword(password);

        Usuario usuario = obtener(id);
        usuario.setPassword(passwordEncoder.encode(password));

        repository.save(usuario);
    }


}