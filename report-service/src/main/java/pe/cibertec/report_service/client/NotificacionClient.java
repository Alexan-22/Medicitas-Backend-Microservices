package pe.cibertec.report_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "NOTIFICATION-SERVICE")
	public interface NotificacionClient {

	    @GetMapping("/notifications")
	    List<Object> listarNotificaciones();

	}