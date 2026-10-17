package pe.cibertec.report_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;

import feign.RequestInterceptor;

@FeignClient(
		name = "NOTIFICATION-SERVICE",
		configuration = NotificacionClient.ClientConfig.class
)
public interface NotificacionClient {

	@GetMapping("/internal/notifications/count")
	Long contarNotificaciones();

	class ClientConfig {

		@Bean
		public RequestInterceptor notificationReadKey(
				@Value("${NOTIFICATION_READ_KEY}") String clave) {
			return template -> template.header("X-Service-Key", clave);
		}
	}
}