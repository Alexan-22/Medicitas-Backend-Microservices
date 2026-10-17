package pe.cibertec.report_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import feign.RequestInterceptor;

@FeignClient(
		name = "APPOINTMENT-SERVICE",
		configuration = CitaClient.ClientConfig.class
)
public interface CitaClient {

	@GetMapping("/internal/appointments/count")
	Long contarCitas();

	@GetMapping("/internal/appointments/count/{estado}")
	Long contarPorEstado(@PathVariable("estado") String estado);

	class ClientConfig {

		@Bean
		public RequestInterceptor appointmentReadKey(
				@Value("${APPOINTMENT_READ_KEY}") String clave) {
			return template -> template.header("X-Service-Key", clave);
		}
	}
}