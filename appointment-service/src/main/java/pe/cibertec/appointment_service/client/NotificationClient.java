package pe.cibertec.appointment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import pe.cibertec.appointment_service.dto.NotificationDTO;

@FeignClient(name = "NOTIFICATION-SERVICE")
	public interface NotificationClient {

	    @PostMapping("/notifications")
	    void crearNotificacion(
	            @RequestBody NotificationDTO dto);
	}