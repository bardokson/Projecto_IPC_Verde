/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

/**
 * Controlador de la ventana de Estadísticas Acumuladas.
 */

public class EstadisticasController {

    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private Button btnCalcular;
    @FXML private Label lblTiempo;
    @FXML private Label lblDistancia;
    @FXML private Label lblAscenso;
    @FXML private Label lblDescenso;

    
    
    /**
     * Método de inicialización de JavaFX.
     */
    
    
    @FXML
    public void initialize() {

        dpHasta.setValue(LocalDate.now());
        dpDesde.setValue(LocalDate.now().minusMonths(1));

        calcularEstadisticas();
    }

    /**
     * Motor principal de cálculo de estadísticas.
     * Extrae el rango de fechas seleccionado por el usuario y recorre todas las 
     * actividades del perfil activo. 
     * Si una actividad entra en el rango temporal,itera sobre sus TrackPoints para 
     * acumular la distancia total y calcular las diferencias de altitud (ascenso y descenso acumulado).
     * Finalmente, formatea los datos y los inyecta en las etiquetas de la interfaz.
     * 
     * [Código asistido por IA]
     */
    @FXML
    private void calcularEstadisticas() {
        LocalDate fechaDesde = dpDesde.getValue();
        LocalDate fechaHasta = dpHasta.getValue();

        if (fechaDesde == null || fechaHasta == null) {
            mostrarAlerta("Fechas incompletas", "Por favor, selecciona tanto la fecha de inicio como la de fin.");
            return;
        }

        if (fechaDesde.isAfter(fechaHasta)) {
            mostrarAlerta("Rango erróneo", "La fecha de inicio ('Desde') no puede ser posterior a la fecha de fin ('Hasta').");
            return;
        }

        LocalDateTime inicioRange = fechaDesde.atStartOfDay();
        LocalDateTime finRange = fechaHasta.atTime(23, 59, 59);

        SportActivityApp app = SportActivityApp.getInstance();
        User usuarioActivo = app.getCurrentUser();
        
        if (usuarioActivo == null) {return;}

        List<Activity> actividades = usuarioActivo.getActivities();

        long segundosTotales = 0;
        double distanciaTotalMetros = 0;
        double ascensoTotal = 0;
        double descensoTotal = 0;

        for (Activity act : actividades) {
            LocalDateTime fechaActividad = act.getStartTime();

            if (fechaActividad != null && !fechaActividad.isBefore(inicioRange) && !fechaActividad.isAfter(finRange)) {

                if (act.getStartPoint() != null && act.getEndPoint() != null) {
                    long segundosAct = java.time.Duration.between(act.getStartTime(), act.getEndTime()).getSeconds();
                    segundosTotales += segundosAct;
                }

                List<upv.ipc.sportlib.TrackPoint> puntos = act.getTrackPoints();
                if (puntos != null && puntos.size() > 1) {
                    for (int i = 0; i < puntos.size() - 1; i++) {
                        upv.ipc.sportlib.TrackPoint p1 = puntos.get(i);
                        upv.ipc.sportlib.TrackPoint p2 = puntos.get(i + 1);

                        distanciaTotalMetros += p1.distanceTo(p2);

                        double diferenciaAltitud = p2.getElevation() - p1.getElevation();
                        if (diferenciaAltitud > 0) {
                            ascensoTotal += diferenciaAltitud;
                        } else if (diferenciaAltitud < 0) {
                            descensoTotal += Math.abs(diferenciaAltitud);
                        }
                    }
                }
            }
        }

        long horas = segundosTotales / 3600;
        long minutos = (segundosTotales % 3600) / 60;
        long segundos = segundosTotales % 60;
        lblTiempo.setText(String.format("%02d:%02d:%02d", horas, minutos, segundos));

        double distanciaKm = distanciaTotalMetros / 1000.0;
        lblDistancia.setText(String.format("%.2f km", distanciaKm));

        lblAscenso.setText(String.format("%.0f m", ascensoTotal));
        lblDescenso.setText(String.format("%.0f m", descensoTotal));
    }

    /**
     * Metodo auxiliar para crear alertas.
     * Configurado por defecto como una alerta de tipo WARNING
     * @param titulo titulo para la alerta
     * @param mensaje titulo para el mensaje
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
