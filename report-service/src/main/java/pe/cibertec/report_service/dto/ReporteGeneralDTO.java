package pe.cibertec.report_service.dto;

import lombok.Data;

@Data
public class ReporteGeneralDTO {

    private Long totalPacientes;

    private Long totalMedicos;

    private Long totalCitas;

    private Long totalNotificaciones;
}