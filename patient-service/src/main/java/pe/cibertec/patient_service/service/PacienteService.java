package pe.cibertec.patient_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import pe.cibertec.patient_service.entity.Paciente;
import pe.cibertec.patient_service.repository.PacienteRepository;
import java.time.LocalDate;
import org.springframework.dao.DataIntegrityViolationException;
import pe.cibertec.patient_service.dto.CrearPerfilRequest;

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

    public Paciente crearMiPerfil(Long usuarioId, CrearPerfilRequest datos) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Usuario no válido");
        }

        if (repository.existsByUsuarioId(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Tu cuenta ya tiene un perfil de paciente");
        }

        if (datos == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Completa tus datos");
        }

        String dni = limpiar(datos.getDni());
        String nombres = limpiar(datos.getNombres());
        String apellidos = limpiar(datos.getApellidos());
        String sexo = limpiar(datos.getSexo()).toUpperCase(java.util.Locale.ROOT);
        String telefono = limpiar(datos.getTelefono());
        String email = limpiar(datos.getEmail());

        if (!dni.matches("[0-9]{8}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El DNI debe tener 8 dígitos");
        }

        if (nombres.isBlank() || apellidos.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ingresa tus nombres y apellidos");
        }

        if (datos.getFechaNacimiento() == null
                || datos.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La fecha de nacimiento no es válida");
        }

        if (!List.of("M", "F").contains(sexo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selecciona el sexo: M o F");
        }

        if (!telefono.isEmpty() && !telefono.matches("[0-9]{9}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El teléfono debe tener 9 dígitos");
        }

        if (!email.isEmpty() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El correo no es válido");
        }

        if (repository.existsByDni(dni)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una ficha con ese DNI. Contacta al administrador para revisar la vinculación");
        }

        Paciente paciente = new Paciente();
        paciente.setUsuarioId(usuarioId);
        paciente.setDni(dni);
        paciente.setNombres(nombres);
        paciente.setApellidos(apellidos);
        paciente.setFechaNacimiento(datos.getFechaNacimiento());
        paciente.setSexo(sexo);
        paciente.setTelefono(telefono);
        paciente.setEmail(email);
        paciente.setDireccion(limpiar(datos.getDireccion()));
        paciente.setTipoSangre(limpiar(datos.getTipoSangre()));
        paciente.setAlergias(limpiar(datos.getAlergias()));
        paciente.setEstado("ACTIVO");
        paciente.setFechaRegistro(LocalDateTime.now());

        try {
            return repository.saveAndFlush(paciente);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pudo crear el perfil. La cuenta o el DNI podrían estar registrados");
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}