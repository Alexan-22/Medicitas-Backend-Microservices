package pe.cibertec.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.cibertec.notification_service.entity.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

}
