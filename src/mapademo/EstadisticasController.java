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

public class EstadisticasController {

    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private Button btnCalcular;
    
    // Etiquetas donde mostraremos los resultados
    @FXML private Label lblTiempo;
    @FXML private Label lblDistancia;
    @FXML private Label lblAscenso;
    @FXML private Label lblDescenso;

    @FXML
    public void initialize() {
        // Por defecto, podemos poner que la fecha 'Hasta' sea el día de hoy
        dpHasta.setValue(LocalDate.now());
        // Y la fecha 'Desde' hace un mes, para que no salga vacío al arrancar
        dpDesde.setValue(LocalDate.now().minusMonths(1));
        
        // Ejecutamos un primer cálculo automático con ese mes de margen
        calcularEstadisticas(null);
    }

    @FXML
    private void calcularEstadisticas(ActionEvent event) {
        LocalDate fechaDesde = dpDesde.getValue();
        LocalDate fechaHasta = dpHasta.getValue();

        // Validación defensiva por si el usuario borra las fechas a mano
        if (fechaDesde == null || fechaHasta == null) {
            mostrarAlerta("Fechas incompletas", "Por favor, selecciona tanto la fecha de inicio como la de fin.");
            return;
        }

        // Validación lógica: que la fecha de inicio no sea mayor que la de fin
        if (fechaDesde.isAfter(fechaHasta)) {
            mostrarAlerta("Rango erróneo", "La fecha de inicio ('Desde') no puede ser posterior a la fecha de fin ('Hasta').");
            return;
        }

        // Convertimos las fechas locales a LocalDateTime para poder compararlas con la librería
        // 'Desde' empezará a las 00:00:00 y 'Hasta' terminará a las 23:59:59 de ese día
        LocalDateTime inicioRange = fechaDesde.atStartOfDay();
        LocalDateTime finRange = fechaHasta.atTime(23, 59, 59);

        // Obtenemos el usuario actual y sus actividades de la librería
        SportActivityApp app = SportActivityApp.getInstance();
        User usuarioActivo = app.getCurrentUser();
        
        if (usuarioActivo == null) {
            return; // Si no hay usuario logueado, no hacemos nada
        }

        List<Activity> actividades = usuarioActivo.getActivities();

        // Variables acumuladoras para las matemáticas
        long segundosTotales = 0;
        double distanciaTotalMetros = 0;
        double ascensoTotal = 0;
        double descensoTotal = 0;

        // Recorremos todas las actividades del usuario
        for (Activity act : actividades) {
            LocalDateTime fechaActividad = act.getStartTime();

            // Comprobamos si la actividad cae dentro del rango de fechas elegido
            if (fechaActividad != null && !fechaActividad.isBefore(inicioRange) && !fechaActividad.isAfter(finRange)) {
                
                // 1. Sumar duración (calculamos los segundos entre el inicio y el fin de la actividad)
                if (act.getStartPoint() != null && act.getEndPoint() != null) {
                    long segundosAct = java.time.Duration.between(act.getStartTime(), act.getEndTime()).getSeconds();
                    segundosTotales += segundosAct;
                }

                // Extraemos los puntos para calcular distancia y desniveles a mano
                List<upv.ipc.sportlib.TrackPoint> puntos = act.getTrackPoints();
                if (puntos != null && puntos.size() > 1) {
                    for (int i = 0; i < puntos.size() - 1; i++) {
                        upv.ipc.sportlib.TrackPoint p1 = puntos.get(i);
                        upv.ipc.sportlib.TrackPoint p2 = puntos.get(i + 1);

                        // 2. Sumar distancia entre el punto 1 y el punto 2
                        distanciaTotalMetros += p1.distanceTo(p2);

                        // 3 y 4. Calcular la diferencia de altitud para el ascenso/descenso
                        double diferenciaAltitud = p2.getElevation() - p1.getElevation();
                        if (diferenciaAltitud > 0) {
                            ascensoTotal += diferenciaAltitud; // Si sube, es ascenso
                        } else if (diferenciaAltitud < 0) {
                            descensoTotal += Math.abs(diferenciaAltitud); // Si baja, lo sumamos al descenso (en positivo)
                        }
                    }
                }
            }
        }

        // --- FORMATEAR Y MOSTRAR LOS RESULTADOS EN LA INTERFAZ ---

        // Formatear Tiempo Total a HH:mm:ss
        long horas = segundosTotales / 3600;
        long minutos = (segundosTotales % 3600) / 60;
        long segundos = segundosTotales % 60;
        lblTiempo.setText(String.format("%02d:%02d:%02d", horas, minutos, segundos));

        // Formatear Distancia Total a Kilómetros (de metros a km)
        double distanciaKm = distanciaTotalMetros / 1000.0;
        lblDistancia.setText(String.format("%.2f km", distanciaKm));

        // Formatear Metros de Ascenso y Descenso (sin decimales, ya que son metros)
        lblAscenso.setText(String.format("%.0f m", ascensoTotal));
        lblDescenso.setText(String.format("%.0f m", descensoTotal));
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
