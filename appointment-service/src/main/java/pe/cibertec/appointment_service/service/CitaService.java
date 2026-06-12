package pe.cibertec.appointment_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

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


    private final CitaRepository repository;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;
    private final NotificationClient notificationClient;

    public List<Cita> listar() {
        return repository.findAll();
    }

    public Cita buscarPorId(Long id) {
        return repository.findById(id)
                .orElse(null);
    }

    public Cita guardar(Cita cita) {

        boolean existe =
                repository.existsByMedicoIdAndFechaAndHora(
                        cita.getMedicoId(),
                        cita.getFecha(),
                        cita.getHora());

        if (existe) {
            throw new RuntimeException(
                    "El médico ya tiene una cita en ese horario");
        }

        cita.setEstado("PROGRAMADA");

        cita.setFechaCreacion(
                LocalDateTime.now());

        if (cita.getUsuarioCreacion() == null) {
            cita.setUsuarioCreacion("admin");
        }

        Cita citaGuardada =
                repository.save(cita);
        
        PacienteDTO paciente =
                pacienteClient.obtenerPaciente(
                        citaGuardada.getPacienteId());

        NotificationDTO dto =
                new NotificationDTO();

        dto.setCitaId(
                citaGuardada.getId());

        dto.setTipo("EMAIL");

        dto.setDestinatario(
                paciente.getEmail());

        dto.setMensaje(
                "Estimado(a) "
                + paciente.getNombres()
                + ", su cita médica ha sido registrada correctamente.");

        notificationClient
                .crearNotificacion(dto);

        return citaGuardada;
    }

    public Cita actualizar(Long id, Cita cita) {

        Cita existente =
                repository.findById(id)
                .orElseThrow();

        existente.setPacienteId(
                cita.getPacienteId());

        existente.setMedicoId(
                cita.getMedicoId());

        existente.setFecha(
                cita.getFecha());

        existente.setHora(
                cita.getHora());

        existente.setEstado(
                cita.getEstado());
        
        existente.setMotivoConsulta(
                cita.getMotivoConsulta());

        existente.setObservaciones(
                cita.getObservaciones());

        existente.setEstado(
                cita.getEstado());

        return repository.save(existente);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
    
    public CitaDetalleDTO obtenerDetalle(Long id) {

        Cita cita = repository.findById(id)
                .orElseThrow();

        PacienteDTO paciente =
                pacienteClient.obtenerPaciente(
                        cita.getPacienteId());

        MedicoDTO medico =
                medicoClient.obtenerMedico(
                        cita.getMedicoId());

        CitaDetalleDTO dto =
                new CitaDetalleDTO();

        dto.setId(cita.getId());

        dto.setPaciente(
                paciente.getNombres()
                + " "
                + paciente.getApellidos());

        dto.setMedico(
                medico.getNombres());

        dto.setEspecialidad(
                medico.getEspecialidad());

        dto.setFecha(
                cita.getFecha());

        dto.setHora(
                cita.getHora());

        dto.setEstado(
                cita.getEstado());

        return dto;
    }
    
    public Long contarPorEstado(String estado) {
        return repository.countByEstado(estado);
    }
    
    public Cita atender(Long id) {

        Cita cita = repository.findById(id)
                .orElseThrow();

        cita.setEstado("ATENDIDA");

        Cita actualizada =
                repository.save(cita);

        generarNotificacion(
                actualizada.getId(),
                actualizada.getPacienteId(),
                "Su cita médica ha sido atendida correctamente.");

        return actualizada;
    }
    
    public Cita cancelar(Long id) {

        Cita cita = repository.findById(id)
                .orElseThrow();

        cita.setEstado("CANCELADA");

        Cita actualizada =
                repository.save(cita);

        generarNotificacion(
                actualizada.getId(),
                actualizada.getPacienteId(),
                "Su cita médica ha sido cancelada.");

        return actualizada;
    }
    
    public Cita noAsistio(Long id) {

        Cita cita = repository.findById(id)
                .orElseThrow();

        cita.setEstado("NO_ASISTIO");

        Cita actualizada =
                repository.save(cita);

        generarNotificacion(
                actualizada.getId(),
                actualizada.getPacienteId(),
                "Se registró su inasistencia a la cita médica.");

        return actualizada;
    }
    
    private void generarNotificacion(
            Long citaId,
            Long pacienteId,
            String mensaje) {

        PacienteDTO paciente =
                pacienteClient.obtenerPaciente(
                        pacienteId);

        NotificationDTO dto =
                new NotificationDTO();

        dto.setCitaId(citaId);

        dto.setTipo("EMAIL");

        dto.setDestinatario(
                paciente.getEmail());

        dto.setMensaje(mensaje);

        notificationClient
                .crearNotificacion(dto);
    }
    
}
