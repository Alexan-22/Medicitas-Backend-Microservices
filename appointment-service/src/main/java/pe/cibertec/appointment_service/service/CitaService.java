package pe.cibertec.appointment_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import pe.cibertec.appointment_service.client.MedicoClient;
import pe.cibertec.appointment_service.client.NotificationClient;
import pe.cibertec.appointment_service.client.PacienteClient;
import pe.cibertec.appointment_service.dto.CitaDetalleDTO;
import pe.cibertec.appointment_service.dto.MedicoDTO;
import pe.cibertec.appointment_service.dto.NotificationDTO;
import pe.cibertec.appointment_service.dto.PacienteDTO;
import pe.cibertec.appointment_service.entity.Cita;
import pe.cibertec.appointment_service.repository.CitaRepository;

@Service
@RequiredArgsConstructor
public class CitaService {

    private static final int DURACION_MINUTOS = 30;
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final CitaRepository repository;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;
    private final NotificationClient notificationClient;

    // CONSULTAS GENERALES
    public List<Cita> listar() {
        return repository.findAll();
    }

    public Cita buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    public List<CitaDetalleDTO> listarDetalle() {
        return repository.findAll().stream()
                .map(this::crearDetalle)
                .toList();
    }

    public CitaDetalleDTO obtenerDetalle(Long id) {
        return crearDetalle(buscarPorId(id));
    }

    // CITAS DEL PACIENTE AUTENTICADO
    public List<CitaDetalleDTO> listarMisCitas(String token) {
        validarToken(token);

        PacienteDTO paciente;

        try {
            paciente = pacienteClient.obtenerMiPerfil("Bearer " + token);
        } catch (FeignException exception) {
            if (exception.status() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Esta cuenta no tiene un paciente vinculado");
            }

            if (exception.status() == 401 || exception.status() == 403) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "No se pudo validar la sesión del paciente");
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar el servicio de pacientes");
        }

        if (paciente == null || paciente.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontró el perfil del paciente");
        }

        return repository
                .findByPacienteIdOrderByFechaDescHoraDesc(paciente.getId())
                .stream()
                .map(this::crearDetalle)
                .toList();
    }

    // CITAS DEL MÉDICO AUTENTICADO
    public List<CitaDetalleDTO> listarMisCitasMedico(String token) {
        validarToken(token);

        MedicoDTO medico;

        try {
            medico = medicoClient.obtenerMiPerfil("Bearer " + token);
        } catch (FeignException exception) {
            if (exception.status() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Esta cuenta no tiene un médico vinculado");
            }

            if (exception.status() == 401) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "No se pudo validar la sesión del médico");
            }

            if (exception.status() == 403) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "La cuenta no tiene permiso para consultar el perfil médico");
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar el servicio de médicos");
        }

        if (medico == null || medico.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "El servicio de médicos devolvió un perfil no válido");
        }

        return repository
                .findByMedicoIdOrderByFechaDescHoraDesc(medico.getId())
                .stream()
                .map(this::crearDetalle)
                .toList();
    }

    private void validarToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Se requiere autenticación");
        }
    }

    private CitaDetalleDTO crearDetalle(Cita cita) {
        PacienteDTO paciente =
                pacienteClient.obtenerPaciente(cita.getPacienteId());

        MedicoDTO medico =
                medicoClient.obtenerMedico(cita.getMedicoId());

        if (paciente == null || medico == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se encontró el paciente o médico de la cita");
        }

        CitaDetalleDTO dto = new CitaDetalleDTO();
        dto.setId(cita.getId());
        dto.setPaciente(nombreCompleto(
                paciente.getNombres(), paciente.getApellidos()));
        dto.setMedico(nombreCompleto(
                medico.getNombres(), medico.getApellidos()));
        dto.setEspecialidad(medico.getEspecialidad());
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setEstado(cita.getEstado());

        return dto;
    }

    public Long contarPorEstado(String estado) {
        return repository.countByEstado(estado);
    }

    // DISPONIBILIDAD
    public List<String> obtenerDisponibles(Long medicoId, LocalDate fecha) {
        return calcularDisponibles(medicoId, fecha, null);
    }

    private List<String> calcularDisponibles(
            Long medicoId, LocalDate fecha, Long excluirCitaId) {

        if (medicoId == null || medicoId <= 0 || fecha == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selecciona médico y fecha");
        }

        if (fecha.isBefore(LocalDate.now())) {
            return List.of();
        }

        MedicoDTO medico = medicoClient.obtenerMedico(medicoId);

        if (medico == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Médico no encontrado");
        }

        if (!"ACTIVO".equalsIgnoreCase(medico.getEstado())) {
            return List.of();
        }

        LocalTime inicio = medico.getHorarioInicio();
        LocalTime fin = medico.getHorarioFin();

        if (inicio == null || fin == null || !inicio.isBefore(fin)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El médico no tiene un horario válido configurado");
        }

        Set<LocalTime> ocupadas = repository
                .findByMedicoIdAndFecha(medicoId, fecha)
                .stream()
                .filter(c -> excluirCitaId == null
                        || !Objects.equals(c.getId(), excluirCitaId))
                .filter(c -> !"CANCELADA".equalsIgnoreCase(c.getEstado()))
                .map(Cita::getHora)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<String> disponibles = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        int inicioMinutos = inicio.getHour() * 60 + inicio.getMinute();
        int finMinutos = fin.getHour() * 60 + fin.getMinute();

        for (int minuto = inicioMinutos;
             minuto + DURACION_MINUTOS <= finMinutos;
             minuto += DURACION_MINUTOS) {

            LocalTime hora = LocalTime.of(minuto / 60, minuto % 60);

            if (!ocupadas.contains(hora)
                    && LocalDateTime.of(fecha, hora).isAfter(ahora)) {
                disponibles.add(hora.format(FORMATO_HORA));
            }
        }

        return disponibles;
    }

    private void validarHorario(Cita cita, Long excluirCitaId) {
        if (cita.getPacienteId() == null || cita.getPacienteId() <= 0
                || cita.getMedicoId() == null || cita.getMedicoId() <= 0
                || cita.getFecha() == null || cita.getHora() == null
                || cita.getMotivoConsulta() == null
                || cita.getMotivoConsulta().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completa paciente, médico, fecha, hora y motivo");
        }

        if (cita.getHora().getSecond() != 0
                || cita.getHora().getNano() != 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora debe tener precisión de minutos");
        }

        String hora = cita.getHora().format(FORMATO_HORA);

        if (!calcularDisponibles(
                cita.getMedicoId(), cita.getFecha(), excluirCitaId)
                .contains(hora)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La hora seleccionada no está disponible");
        }
    }

    // REGISTRAR Y ACTUALIZAR
    public Cita guardar(Cita cita) {
        validarHorario(cita, null);

        PacienteDTO paciente =
                pacienteClient.obtenerPaciente(cita.getPacienteId());

        if (paciente == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Paciente no encontrado");
        }

        cita.setId(null);
        cita.setEstado("PROGRAMADA");
        cita.setFechaCreacion(LocalDateTime.now());

        if (cita.getUsuarioCreacion() == null
                || cita.getUsuarioCreacion().isBlank()) {
            cita.setUsuarioCreacion("admin");
        }

        Cita guardada = repository.save(cita);

        enviarNotificacion(
                guardada.getId(),
                paciente,
                "Estimado(a) " + paciente.getNombres()
                        + ", su cita médica ha sido registrada correctamente.");

        return guardada;
    }

    public Cita actualizar(Long id, Cita cita) {
        Cita existente = buscarPorId(id);

        if (!"PROGRAMADA".equals(existente.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden editar citas programadas");
        }

        validarHorario(cita, id);

        PacienteDTO paciente =
                pacienteClient.obtenerPaciente(cita.getPacienteId());

        if (paciente == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Paciente no encontrado");
        }

        existente.setPacienteId(cita.getPacienteId());
        existente.setMedicoId(cita.getMedicoId());
        existente.setFecha(cita.getFecha());
        existente.setHora(cita.getHora());
        existente.setMotivoConsulta(cita.getMotivoConsulta());
        existente.setObservaciones(cita.getObservaciones());

        // Los cambios de estado usan sus endpoints específicos.
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        repository.delete(buscarPorId(id));
    }

    // CAMBIOS DE ESTADO
    public Cita atender(Long id) {
        return cambiarEstado(
                id, "ATENDIDA",
                "Su cita médica ha sido atendida correctamente.");
    }

    public Cita cancelar(Long id) {
        return cambiarEstado(
                id, "CANCELADA",
                "Su cita médica ha sido cancelada.");
    }

    public Cita noAsistio(Long id) {
        return cambiarEstado(
                id, "NO_ASISTIO",
                "Se registró su inasistencia a la cita médica.");
    }

    private Cita cambiarEstado(Long id, String estado, String mensaje) {
        Cita cita = buscarPorId(id);

        if (!"PROGRAMADA".equals(cita.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se puede cambiar el estado de una cita programada");
        }

        PacienteDTO paciente =
                pacienteClient.obtenerPaciente(cita.getPacienteId());

        if (paciente == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Paciente no encontrado");
        }

        cita.setEstado(estado);
        Cita actualizada = repository.save(cita);

        enviarNotificacion(actualizada.getId(), paciente, mensaje);
        return actualizada;
    }

    // NOTIFICACIONES
    private void enviarNotificacion(
            Long citaId, PacienteDTO paciente, String mensaje) {

        NotificationDTO dto = new NotificationDTO();
        dto.setCitaId(citaId);
        dto.setTipo("EMAIL");
        dto.setDestinatario(paciente.getEmail());
        dto.setMensaje(mensaje);

        notificationClient.crearNotificacion(dto);
    }

    private String nombreCompleto(String nombres, String apellidos) {
        return ((nombres == null ? "" : nombres)
                + " " + (apellidos == null ? "" : apellidos)).trim();
    }
    public void validarAccesoMedico(Long citaId, String token) {
        validarToken(token);
        Cita cita = buscarPorId(citaId);
        MedicoDTO medico;

        try {
            medico = medicoClient.obtenerMiPerfil("Bearer " + token);
        } catch (FeignException exception) {
            if (exception.status() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "La cuenta no tiene un médico vinculado");
            }

            if (exception.status() == 401 || exception.status() == 403) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No se pudo autorizar al médico");
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar el servicio de médicos");
        }

        if (medico == null || medico.getId() == null
                || !Objects.equals(cita.getMedicoId(), medico.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para acceder a esta cita");
        }
    }

    public Long contarTodas() {
        return repository.count();
    }
}

