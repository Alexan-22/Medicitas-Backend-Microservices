package pe.cibertec.doctor_service.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.cibertec.doctor_service.entity.Medico;
import pe.cibertec.doctor_service.repository.MedicoRepository;

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository repository;
    private final RestTemplate restTemplate;

    public List<Medico> listar() {
        return repository.findAll();
    }

    public Medico guardar(Medico medico) {
        medico.setId(null);
        medico.setUsuarioId(null);

        if (medico.getEstado() == null || medico.getEstado().isBlank()) {
            medico.setEstado("ACTIVO");
        }

        return repository.save(medico);
    }

    public Medico buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Médico no encontrado"));
    }

    public Medico buscarPorUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Esta cuenta no tiene un médico vinculado"));
    }

    public Medico actualizar(Long id, Medico medico) {
        Medico existente = buscarPorId(id);

        existente.setCmp(medico.getCmp());
        existente.setNombres(medico.getNombres());
        existente.setApellidos(medico.getApellidos());
        existente.setEspecialidad(medico.getEspecialidad());
        existente.setTelefono(medico.getTelefono());
        existente.setEmail(medico.getEmail());
        existente.setConsultorio(medico.getConsultorio());
        existente.setHorarioInicio(medico.getHorarioInicio());
        existente.setHorarioFin(medico.getHorarioFin());
        existente.setEstado(medico.getEstado());

        // La edición general conserva la vinculación existente.
        return repository.save(existente);
    }

    public Medico vincularUsuario(Long medicoId, Long usuarioId, String token) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selecciona un usuario válido");
        }

        Medico medico = buscarPorId(medicoId);

        if (medico.getUsuarioId() != null
                && !medico.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este médico ya está vinculado con otra cuenta");
        }

        repository.findByUsuarioId(usuarioId).ifPresent(otro -> {
            if (!otro.getId().equals(medicoId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "La cuenta ya está vinculada con otro médico");
            }
        });

        UsuarioCuenta cuenta = consultarCuenta(usuarioId, token);

        if (!"MEDICO".equals(cuenta.rol())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cuenta seleccionada debe tener el rol MEDICO");
        }

        medico.setUsuarioId(usuarioId);

        try {
            return repository.saveAndFlush(medico);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pudo vincular. Revisa si la cuenta ya está asignada");
        }
    }

    private UsuarioCuenta consultarCuenta(Long usuarioId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        try {
            UsuarioCuenta cuenta = restTemplate.exchange(
                    "http://AUTH-SERVICE/users/" + usuarioId,
                    HttpMethod.GET,
                    new HttpEntity<Void>(headers),
                    UsuarioCuenta.class
            ).getBody();

            if (cuenta == null || !usuarioId.equals(cuenta.id())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Auth Service devolvió una cuenta no válida");
            }

            return cuenta;
        } catch (HttpClientErrorException exception) {
            int estado = exception.getStatusCode().value();

            if (estado == 404) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "La cuenta de usuario no existe");
            }

            if (estado == 401 || estado == 403) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No tienes autorización para consultar esta cuenta");
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar la cuenta en Auth Service");
        } catch (RestClientException | IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Auth Service no está disponible. Intenta nuevamente");
        }
    }

    public void eliminar(Long id) {
        repository.delete(buscarPorId(id));
    }

    public Long contarActivos() {
        return repository.countByEstado("ACTIVO");
    }

    public record UsuarioCuenta(Long id, String username, String rol) {}
}
