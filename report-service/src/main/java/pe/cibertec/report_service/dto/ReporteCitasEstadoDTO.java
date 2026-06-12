package pe.cibertec.report_service.dto;

import lombok.Data;

@Data
public class ReporteCitasEstadoDTO {

    private Long programadas;

    private Long atendidas;

    private Long canceladas;

    private Long noAsistio;
    
}
