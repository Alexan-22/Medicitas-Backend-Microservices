package pe.cibertec.patient_service.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import pe.cibertec.patient_service.entity.Paciente;
import pe.cibertec.patient_service.service.PacienteService;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService service;

    @GetMapping
    public List<Paciente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Paciente buscarPorId(
            @PathVariable Long id) {

        return service.buscarPorId(id);
    }

    @PostMapping
    public Paciente guardar(
            @RequestBody Paciente paciente) {

        return service.guardar(paciente);
    }

    @PutMapping("/{id}")
    public Paciente actualizar(
            @PathVariable Long id,
            @RequestBody Paciente paciente) {

        return service.actualizar(id,
                paciente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id) {

        service.eliminar(id);
    }
    
    @GetMapping("/count/activos")
    public Long contarActivos() {
        return service.contarActivos();
    }
}
