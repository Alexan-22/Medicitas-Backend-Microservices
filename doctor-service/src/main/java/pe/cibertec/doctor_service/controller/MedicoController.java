package pe.cibertec.doctor_service.controller;



import org.springframework.web.bind.annotation.*;
import pe.cibertec.doctor_service.entity.Medico;
import pe.cibertec.doctor_service.service.MedicoService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService service;

    @GetMapping
    public List<Medico> listar(){
    	return service.listar();
    }

    @GetMapping("/{id}")
    public Medico buscarPorId(
            @PathVariable Long id) {

        return service.buscarPorId(id);
    }

    @PostMapping
    public Medico guardar(@RequestBody Medico medico) {

        return service.guardar(medico);
    }

    @PutMapping("/{id}")
    public Medico actualizar(
            @PathVariable Long id,
            @RequestBody Medico medico) {

        return service.actualizar(id,
                medico);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id){
    	service.eliminar(id);
    }
    
    @GetMapping("/count/activos")
    public Long contarActivos() {
        return service.contarActivos();
    }
}