package pe.cibertec.appointment_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public Cita buscarPorId(
            @PathVariable Long id) {

        return service.buscarPorId(id);
    }

    @PostMapping
    public Cita guardar(
            @RequestBody Cita cita) {

        return service.guardar(cita);
    }

    @PutMapping("/{id}")
    public Cita actualizar(
            @PathVariable Long id,
            @RequestBody Cita cita) {

        return service.actualizar(id, cita);
    }

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id) {

        service.eliminar(id);
    }
    
    @GetMapping("/detail/{id}")
    public CitaDetalleDTO detalle(
            @PathVariable Long id){

        return service.obtenerDetalle(id);
    }
    
    @GetMapping("/count/{estado}")
    public Long contarPorEstado(
            @PathVariable String estado) {

        return service.contarPorEstado(estado);
    }
    
    @PutMapping("/{id}/atender")
    public Cita atender(
            @PathVariable Long id){

        return service.atender(id);
    }
    
    @PutMapping("/{id}/cancelar")
    public Cita cancelar(
            @PathVariable Long id){

        return service.cancelar(id);
    }
    
    @PutMapping("/{id}/no-asistio")
    public Cita noAsistio(
            @PathVariable Long id){

        return service.noAsistio(id);
    }
}