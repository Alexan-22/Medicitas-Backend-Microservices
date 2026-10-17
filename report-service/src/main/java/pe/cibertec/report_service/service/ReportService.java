package pe.cibertec.report_service.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import pe.cibertec.report_service.client.CitaClient;
import pe.cibertec.report_service.client.MedicoClient;
import pe.cibertec.report_service.client.NotificacionClient;
import pe.cibertec.report_service.client.PacienteClient;
import pe.cibertec.report_service.dto.ReporteActivosDTO;
import pe.cibertec.report_service.dto.ReporteCitasEstadoDTO;
import pe.cibertec.report_service.dto.ReporteGeneralDTO;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;
    private final CitaClient citaClient;
    private final NotificacionClient notificacionClient;

    public ReporteGeneralDTO obtenerReporte() {
        ReporteGeneralDTO dto = new ReporteGeneralDTO();
        dto.setTotalPacientes((long) pacienteClient.listarPacientes().size());
        dto.setTotalMedicos((long) medicoClient.listarMedicos().size());
        dto.setTotalCitas(citaClient.contarCitas());
        dto.setTotalNotificaciones(notificacionClient.contarNotificaciones());
        return dto;
    }

    public ReporteCitasEstadoDTO obtenerReporteEstado() {
        ReporteCitasEstadoDTO dto = new ReporteCitasEstadoDTO();
        dto.setProgramadas(citaClient.contarPorEstado("PROGRAMADA"));
        dto.setAtendidas(citaClient.contarPorEstado("ATENDIDA"));
        dto.setCanceladas(citaClient.contarPorEstado("CANCELADA"));
        dto.setNoAsistio(citaClient.contarPorEstado("NO_ASISTIO"));
        return dto;
    }

    public ReporteActivosDTO obtenerActivos() {
        ReporteActivosDTO dto = new ReporteActivosDTO();
        dto.setPacientesActivos(pacienteClient.contarActivos());
        dto.setMedicosActivos(medicoClient.contarActivos());
        return dto;
    }
}
