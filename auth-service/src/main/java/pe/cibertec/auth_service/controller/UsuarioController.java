package pe.cibertec.auth_service.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.auth_service.dto.UsuarioRequest;
import pe.cibertec.auth_service.dto.UsuarioResponse;
import pe.cibertec.auth_service.service.UsuarioService;
import pe.cibertec.auth_service.dto.ChangePasswordRequest;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable("id") Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse guardar(@RequestBody UsuarioRequest request) {
        return service.guardar(request);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(
            @PathVariable("id") Long id,
            @RequestBody UsuarioRequest request) {
        return service.actualizar(id, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> manejarError(
            ResponseStatusException exception) {
        String mensaje = exception.getReason() == null
                ? "No se pudo procesar la solicitud"
                : exception.getReason();

        return ResponseEntity.status(exception.getStatusCode())
                .body(Map.of("mensaje", mensaje));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Jwt jwt) {

        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Se requiere autenticación");
        }

        Object claim = jwt.getClaims().get("userId");

        if (!(claim instanceof Number numero) || numero.longValue() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "El token no contiene un userId válido");
        }

        service.eliminar(id, numero.longValue());
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, String>> cambiarPassword(
            @PathVariable("id") Long id,
            @RequestBody ChangePasswordRequest request) {

        service.cambiarPassword(id, request.getPassword());

        return ResponseEntity.ok(
                Map.of("mensaje", "Contraseña actualizada correctamente"));
    }

}