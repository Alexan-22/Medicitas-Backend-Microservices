package pe.cibertec.patient_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import pe.cibertec.patient_service.entity.Paciente;
import pe.cibertec.patient_service.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public List<Paciente> listar() {
        return repository.findAll();
    }

    public Paciente guardar(Paciente paciente) {
        paciente.setEstado("ACTIVO");
        paciente.setFechaRegistro(LocalDateTime.now());
        return repository.save(paciente);
    }

    public Paciente buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Paciente buscarPorUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Esta cuenta no tiene un paciente vinculado"));
    }

    public Paciente actualizar(Long id, Paciente paciente) {
        Paciente existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Paciente no encontrado"));

        existente.setDni(paciente.getDni());
        existente.setNombres(paciente.getNombres());
        existente.setApellidos(paciente.getApellidos());
        existente.setFechaNacimiento(paciente.getFechaNacimiento());
        existente.setSexo(paciente.getSexo());
        existente.setTelefono(paciente.getTelefono());
        existente.setEmail(paciente.getEmail());
        existente.setDireccion(paciente.getDireccion());
        existente.setTipoSangre(paciente.getTipoSangre());
        existente.setAlergias(paciente.getAlergias());
        existente.setEstado(paciente.getEstado());

        return repository.save(existente);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public Long contarActivos() {
        return repository.countByEstado("ACTIVO");
    }
}