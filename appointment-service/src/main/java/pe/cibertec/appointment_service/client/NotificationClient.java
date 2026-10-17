package pe.cibertec.appointment_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import feign.RequestInterceptor;
import pe.cibertec.appointment_service.dto.NotificationDTO;

@FeignClient(
		name = "NOTIFICATION-SERVICE",
		configuration = NotificationClient.ClientConfig.class
)
public interface NotificationClient {

	@PostMapping("/internal/notifications")
	void crearNotificacion(@RequestBody NotificationDTO dto);

	class ClientConfig {

		@Bean
		public RequestInterceptor notificationWriteKey(
				@Value("${NOTIFICATION_WRITE_KEY}") String clave) {
			return template -> template.header("X-Service-Key", clave);
		}
	}
}