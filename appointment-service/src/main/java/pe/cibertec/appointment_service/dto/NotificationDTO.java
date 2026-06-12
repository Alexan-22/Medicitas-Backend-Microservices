package pe.cibertec.appointment_service.dto;

import lombok.Data;

@Data
public class NotificationDTO {

    private Long citaId;

    private String tipo;

    private String destinatario;

    private String mensaje;
}
