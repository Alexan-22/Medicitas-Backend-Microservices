package pe.cibertec.doctor_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.doctor_service.entity.Medico;
import pe.cibertec.doctor_service.service.MedicoService;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService service;

    @GetMapping
    public List<Medico> listar() {
        return service.listar();
    }

    @GetMapping("/me")
    public Medico miPerfil(@AuthenticationPrincipal Jwt jwt) {
        return service.buscarPorUsuarioId(obtenerUsuarioId(jwt));
    }

    @PutMapping("/{id}/usuario")
    public Medico vincularUsuario(
            @PathVariable("id") Long id,
            @RequestBody VincularUsuarioRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        obtenerUsuarioId(jwt);
        return service.vincularUsuario(
                id, request.usuarioId(), jwt.getTokenValue());
    }

    @GetMapping("/{id}")
    public Medico buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Medico guardar(@RequestBody Medico medico) {
        return service.guardar(medico);
    }

    @PutMapping("/{id}")
    public Medico actualizar(
            @PathVariable("id") Long id,
            @RequestBody Medico medico) {
        return service.actualizar(id, medico);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
    }

    @GetMapping("/count/activos")
    public Long contarActivos() {
        return service.contarActivos();
    }

    private Long obtenerUsuarioId(Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Se requiere autenticación");
        }

        Object claim = jwt.getClaims().get("userId");

        if (!(claim instanceof Number numero) || numero.longValue() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "El token no contiene un userId válido");
        }

        return numero.longValue();
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

    public record VincularUsuarioRequest(Long usuarioId) {}
}