package pe.cibertec.patient_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import pe.cibertec.patient_service.dto.CrearPerfilRequest;
import pe.cibertec.patient_service.entity.Paciente;
import pe.cibertec.patient_service.service.PacienteService;

@RestController
@RequestMapping("/patients")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Paciente> listar() {
        return service.listar();
    }

    @GetMapping("/me")
    public Paciente miPerfil(@AuthenticationPrincipal Jwt jwt) {
        return service.buscarPorUsuarioId(obtenerUsuarioId(jwt));
    }

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente crearMiPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CrearPerfilRequest datos) {
        return service.crearMiPerfil(obtenerUsuarioId(jwt), datos);
    }

    @GetMapping("/{id}")
    public Paciente buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Paciente guardar(@RequestBody Paciente paciente) {
        return service.guardar(paciente);
    }

    @PutMapping("/{id}")
    public Paciente actualizar(
            @PathVariable("id") Long id,
            @RequestBody Paciente paciente) {
        return service.actualizar(id, paciente);
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
}