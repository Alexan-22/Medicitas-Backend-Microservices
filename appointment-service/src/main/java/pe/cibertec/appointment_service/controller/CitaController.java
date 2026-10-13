package pe.cibertec.appointment_service.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.appointment_service.dto.CitaDetalleDTO;
import pe.cibertec.appointment_service.entity.Cita;
import pe.cibertec.appointment_service.service.CitaService;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService service;

    @GetMapping
    public List<Cita> listar() {
        return service.listar();
    }

    @GetMapping("/me")
    public List<CitaDetalleDTO> misCitas(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Se requiere autenticación");
        }

        return service.listarMisCitas(jwt.getTokenValue());
    }

    @GetMapping("/listado")
    public List<CitaDetalleDTO> listarDetalle() {
        return service.listarDetalle();
    }

    @GetMapping("/{id}")
    public Cita buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Cita guardar(@RequestBody Cita cita) {
        return service.guardar(cita);
    }

    @PutMapping("/{id}")
    public Cita actualizar(
            @PathVariable("id") Long id,
            @RequestBody Cita cita) {
        return service.actualizar(id, cita);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
    }

    @GetMapping("/detail/{id}")
    public CitaDetalleDTO detalle(@PathVariable("id") Long id) {
        return service.obtenerDetalle(id);
    }

    @GetMapping("/count/{estado}")
    public Long contarPorEstado(@PathVariable("estado") String estado) {
        return service.contarPorEstado(estado);
    }

    @GetMapping("/disponibles/{medicoId}/{fecha}")
    public List<String> disponibles(
            @PathVariable("medicoId") Long medicoId,
            @PathVariable("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.obtenerDisponibles(medicoId, fecha);
    }

    @PutMapping("/{id}/atender")
    public Cita atender(@PathVariable("id") Long id) {
        return service.atender(id);
    }

    @PutMapping("/{id}/cancelar")
    public Cita cancelar(@PathVariable("id") Long id) {
        return service.cancelar(id);
    }

    @PutMapping("/{id}/no-asistio")
    public Cita noAsistio(@PathVariable("id") Long id) {
        return service.noAsistio(id);
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