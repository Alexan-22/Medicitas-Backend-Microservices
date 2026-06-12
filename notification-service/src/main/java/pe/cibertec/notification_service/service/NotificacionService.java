package pe.cibertec.notification_service.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import pe.cibertec.notification_service.entity.Notificacion;
import pe.cibertec.notification_service.repository.NotificacionRepository;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository repository;

    public List<Notificacion> listar() {
        return repository.findAll();
    }

    public Notificacion buscarPorId(Long id) {
        return repository.findById(id)
                .orElse(null);
    }

    public Notificacion guardar(
            Notificacion notificacion) {

        notificacion.setFechaEnvio(
                LocalDateTime.now());

        notificacion.setEstado(
                "ENVIADO");

        return repository.save(
                notificacion);
    }

    public Notificacion actualizar(
            Long id,
            Notificacion notificacion) {

        Notificacion existente =
                repository.findById(id)
                .orElseThrow();

        existente.setTipo(
                notificacion.getTipo());

        existente.setDestinatario(
                notificacion.getDestinatario());

        existente.setMensaje(
                notificacion.getMensaje());

        existente.setEstado(
                notificacion.getEstado());

        return repository.save(
                existente);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
