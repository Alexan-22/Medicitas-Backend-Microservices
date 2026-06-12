package pe.cibertec.doctor_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.cibertec.doctor_service.entity.Medico;
import pe.cibertec.doctor_service.repository.MedicoRepository;


@Service
@RequiredArgsConstructor
public class MedicoService {
	
	private final MedicoRepository repository;

    public List<Medico> listar() {
        return repository.findAll();
    }

    public Medico guardar(Medico medico) {
        if(medico.getEstado() == null ||
           medico.getEstado().isBlank()) {
            medico.setEstado("ACTIVO");
        }
        return repository.save(medico);
    }

    public Medico buscarPorId(Long id) {
        return repository.findById(id)
                .orElse(null);
    }

    public Medico actualizar(Long id,
    		Medico medico) {

    	Medico existente =
                repository.findById(id)
                .orElseThrow();

        existente.setCmp(
                medico.getCmp());

        existente.setNombres(
                medico.getNombres());

        existente.setApellidos(
                medico.getApellidos());

        existente.setEspecialidad(
                medico.getEspecialidad());

        existente.setTelefono(
                medico.getTelefono());

        existente.setEmail(
                medico.getEmail());

        existente.setConsultorio(
                medico.getConsultorio());

        existente.setHorarioInicio(
                medico.getHorarioInicio());

        existente.setHorarioFin(
                medico.getHorarioFin());

        existente.setEstado(
                medico.getEstado());

        return repository.save(existente);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
    
    public Long contarActivos() {
        return repository.countByEstado("ACTIVO");
    }

}
