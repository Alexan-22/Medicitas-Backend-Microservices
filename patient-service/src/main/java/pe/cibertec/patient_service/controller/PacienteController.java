package pe.cibertec.patient_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Se requiere autenticación");
        }

        Object claim = jwt.getClaims().get("userId");

        if (!(claim instanceof Number numero) || numero.longValue() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "El token no contiene un userId válido");
        }

        return service.buscarPorUsuarioId(numero.longValue());
    }

    @GetMapping("/{id}")
    public Paciente buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Paciente guardar(@RequestBody Paciente paciente) {
        return service.guardar(paciente);
    }

    @PutMapping("/{id}")
    public Paciente actualizar(
            @PathVariable Long id, @RequestBody Paciente paciente) {
        return service.actualizar(id, paciente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/count/activos")
    public Long contarActivos() {
        return service.contarActivos();
    }
}