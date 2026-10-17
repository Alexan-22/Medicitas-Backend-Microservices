package pe.cibertec.appointment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import pe.cibertec.appointment_service.dto.MedicoDTO;

@FeignClient(name = "DOCTOR-SERVICE")
public interface MedicoClient {

    @GetMapping("/doctors/{id}")
    MedicoDTO obtenerMedico(@PathVariable("id") Long id);

    @GetMapping("/doctors/me")
    MedicoDTO obtenerMiPerfil(
            @RequestHeader("Authorization") String authorization);
}