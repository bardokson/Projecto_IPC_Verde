/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.TrackPoint;
import upv.ipc.sportlib.GeoUtils;

public class DesnivelController {

    // Tus elementos del FXML (asegúrate de que los fx:id coinciden en Scene Builder)
    @FXML private AreaChart<Number, Number> graficaDesnivel;
    @FXML private NumberAxis ejeX;
    @FXML private NumberAxis ejeY;

    // Guardaremos la actividad actual para usarla luego en los hovers
    private Activity actividadActual;

    @FXML
    public void initialize() {
        // Configuraciones visuales de la gráfica para que quede más limpia
        graficaDesnivel.setLegendVisible(false); // Quitamos la leyenda porque solo hay una línea
        graficaDesnivel.setCreateSymbols(false); // Quitamos los puntitos de cada dato para que la línea sea fluida
        graficaDesnivel.setAnimated(false); // Quitamos la animación de carga que a veces da tirones con muchos datos
    }

    /**
     * Este es el método "mágico" que llamarán tus compañeros desde el Main 
     * pasándole la actividad que el usuario haya cargado.
     */
    public void setActivity(Activity actividad) {
        this.actividadActual = actividad;
        
        // Limpiamos la gráfica por si hubiera una ruta cargada de antes
        graficaDesnivel.getData().clear();

        if (actividad == null || actividad.getTrackPoints().isEmpty()) {
            return;
        }

        // Creamos la "Serie" de datos que formará la línea de la montaña
        XYChart.Series<Number, Number> serieDesnivel = new XYChart.Series<>();
        List<TrackPoint> puntos = actividad.getTrackPoints();

        double distanciaAcumuladaMetros = 0.0;
        TrackPoint puntoAnterior = puntos.get(0);

        // Recorremos todos los puntos GPS del fichero GPX
        for (TrackPoint puntoActual : puntos) {
            // Calculamos la distancia desde el punto anterior al actual y la sumamos
            distanciaAcumuladaMetros += GeoUtils.distance(puntoAnterior, puntoActual);
            
            // Pasamos la distancia a kilómetros (para el eje X)
            double distanciaKm = distanciaAcumuladaMetros / 1000.0;
            
            // Obtenemos la altitud en metros (para el eje Y)
            double altitud = puntoActual.getElevation();

            // Añadimos el dato a la gráfica
            XYChart.Data<Number, Number> datoGrafica = new XYChart.Data<>(distanciaKm, altitud);
            serieDesnivel.getData().add(datoGrafica);

            puntoAnterior = puntoActual;
        }

        // Finalmente, metemos toda la serie de datos en la gráfica
        graficaDesnivel.getData().add(serieDesnivel);
    }
}