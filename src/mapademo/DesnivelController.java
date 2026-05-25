/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.TrackPoint;
import upv.ipc.sportlib.GeoUtils;
import upv.ipc.sportlib.MapProjection;

/*
 * Controlador encargado de la visualización y gestión del perfil de desnivel.
 */

public class DesnivelController {

    @FXML private AreaChart<Number, Number> graficaDesnivel;
    @FXML private NumberAxis ejeX;
    @FXML private NumberAxis ejeY;

    private Activity actividadActual;
    private List<Double> distanciasAcumuladas;

    private Pane mapPane;
    private MapProjection projection;
    private Circle puntoRastreador;
    private javafx.scene.text.Text textoInfoRastreador;
    
    
    /**
     * Inicializa el controlador para mostrar el desnivel de una ruta.
     */
    public void initialize() {
        graficaDesnivel.setLegendVisible(false); 
        graficaDesnivel.setCreateSymbols(false); 
        graficaDesnivel.setAnimated(false); 

        ejeY.setForceZeroInRange(false);

        graficaDesnivel.setOnMouseMoved(this::onMouseMovedGraph);

        graficaDesnivel.setOnMouseExited(e -> {
            if (puntoRastreador != null) puntoRastreador.setVisible(false);
            if (textoInfoRastreador != null) textoInfoRastreador.setVisible(false);
        });
        
        // ¡NUEVO!: Hacemos que la gráfica se estire HORIZONTALMENTE como un muelle
        HBox.setHgrow(graficaDesnivel, Priority.ALWAYS);
        VBox.setVgrow(graficaDesnivel, Priority.ALWAYS); // Lo dejamos por si acaso
    }

    /**
     * Enlaza este controlador con el contexto visual y geográfico del mapa principal.
     * Inicializa los elementos gráficos (un círculo y un texto) que actuarán como 
     * "rastreador" interactivo sobre el mapa cuando el usuario pase el ratón por la gráfica.
     * * @param mapPane    El panel principal donde se dibuja el mapa y las rutas.
     * @param projection Objeto que permite traducir coordenadas GPS a píxeles en pantalla.
     */
    public void setMapContext(Pane mapPane, MapProjection projection) {
        this.mapPane = mapPane;
        this.projection = projection;

        this.puntoRastreador = new Circle(6, Color.BLUE);
        this.puntoRastreador.setId("rastreador");
        this.puntoRastreador.setStroke(Color.WHITE);
        
        this.puntoRastreador.setStrokeWidth(2);
        this.puntoRastreador.setMouseTransparent(true);
        this.puntoRastreador.setVisible(false);

        this.textoInfoRastreador = new javafx.scene.text.Text();
        
        this.textoInfoRastreador.setId("textoRastreador");
        
        this.textoInfoRastreador.setStyle("-fx-font-weight: bold; -fx-fill: #191970;");
        this.textoInfoRastreador.setVisible(false);
        this.textoInfoRastreador.setMouseTransparent(true);

        this.mapPane.getChildren().addAll(puntoRastreador, textoInfoRastreador);
    }
    
    /**
     * Procesa los puntos GPS de la actividad seleccionada para generar la gráfica de altimetría.
     * * [Código asistido por IA]
     * * @param actividad La actividad deportiva seleccionada que contiene el track GPS.
     */
    public void setActivity(Activity actividad) {
        this.actividadActual = actividad;
        this.distanciasAcumuladas = new ArrayList<>();
        graficaDesnivel.getData().clear();

        if (actividad == null || actividad.getTrackPoints().isEmpty()) {
            return;
        }

        XYChart.Series<Number, Number> serieDesnivel = new XYChart.Series<>();
        List<TrackPoint> puntos = actividad.getTrackPoints();

        double distanciaAcumuladaMetros = 0.0;
        TrackPoint puntoAnterior = puntos.get(0);

        for (TrackPoint puntoActual : puntos) {
            distanciaAcumuladaMetros += GeoUtils.distance(puntoAnterior, puntoActual);
            double distanciaKm = distanciaAcumuladaMetros / 1000.0;

            distanciasAcumuladas.add(distanciaKm);

            double altitud = puntoActual.getElevation();
            XYChart.Data<Number, Number> datoGrafica = new XYChart.Data<>(distanciaKm, altitud);
            serieDesnivel.getData().add(datoGrafica);

            puntoAnterior = puntoActual;
        }

        graficaDesnivel.getData().add(serieDesnivel);
    }

    /**
     * Maneja el evento de movimiento del ratón sobre la gráfica de desnivel.
     * Interpola la posición X del ratón para calcular la distancia en kilómetros, 
     * busca el punto GPS real más cercano a esa distancia y actualiza la posición 
     * del "rastreador" (bolita azul y texto) sobre el mapa para sincronizar ambas vistas.
     * * [Código asistido por IA]
     * * @param event El evento de ratón capturado sobre el AreaChart.
     */
    private void onMouseMovedGraph(MouseEvent event) {
        if (actividadActual == null || mapPane == null || projection == null || puntoRastreador == null) {return;}

        double xEnPixels = ejeX.sceneToLocal(event.getSceneX(), event.getSceneY()).getX();
        Number distanciaKmObj = ejeX.getValueForDisplay(xEnPixels);
        if (distanciaKmObj == null) return;
        double distanciaRaton = distanciaKmObj.doubleValue();

        if (distanciaRaton < 0 || distanciasAcumuladas.isEmpty() || distanciaRaton > distanciasAcumuladas.get(distanciasAcumuladas.size() - 1)) {
            puntoRastreador.setVisible(false);
            if (textoInfoRastreador != null) textoInfoRastreador.setVisible(false);
            return;
        }

        int indiceMasCercano = 0;
        double diferenciaMinima = Double.MAX_VALUE;

        for (int i = 0; i < distanciasAcumuladas.size(); i++) {
            double dif = Math.abs(distanciasAcumuladas.get(i) - distanciaRaton);
            if (dif < diferenciaMinima) {
                diferenciaMinima = dif;
                indiceMasCercano = i;
            }
        }

        TrackPoint puntoGPS = actividadActual.getTrackPoints().get(indiceMasCercano);
        Point2D pixelPos = projection.project(puntoGPS);

        puntoRastreador.setCenterX(pixelPos.getX());
        puntoRastreador.setCenterY(pixelPos.getY());
        puntoRastreador.setVisible(true);
        puntoRastreador.toFront(); // Poner siempre encima de todo
        
        textoInfoRastreador.setText(String.format("%.2f km | %.0f m", distanciaRaton, puntoGPS.getElevation()));
        textoInfoRastreador.setX(pixelPos.getX() + 12);
        textoInfoRastreador.setY(pixelPos.getY() - 12);
        textoInfoRastreador.setVisible(true);
        textoInfoRastreador.toFront();
    }

   /**
     * Construye e inyecta dinámicamente la leyenda de colores para los tramos de velocidad.
     */
    public void crearLeyendaVelocidad() {
        
        javafx.scene.layout.VBox leyenda = new javafx.scene.layout.VBox(5);
        leyenda.setAlignment(javafx.geometry.Pos.CENTER);
        leyenda.setStyle("-fx-padding: 0 20px 0 20px;"); // Le damos margen a los lados para que respire

        javafx.scene.control.Label l1 = new javafx.scene.control.Label("▬ Lento (< 35)");
        l1.setTextFill(Color.web("#2c3e50"));
        l1.setStyle("-fx-font-weight: bold;");

        javafx.scene.control.Label l2 = new javafx.scene.control.Label("▬ Medio (35-40)");
        l2.setTextFill(Color.web("#7f8c8d"));
        l2.setStyle("-fx-font-weight: bold;");

        javafx.scene.control.Label l3 = new javafx.scene.control.Label("▬ Rápido (> 40)");
        l3.setTextFill(Color.web("#27ae60"));
        l3.setStyle("-fx-font-weight: bold;");

        leyenda.getChildren().addAll(l1, l2, l3);

        if (graficaDesnivel.getParent() instanceof Pane) {
            Pane padre = (Pane) graficaDesnivel.getParent();
            if (!padre.getChildren().contains(leyenda)) {
                padre.getChildren().add(0, leyenda); // Lo ponemos a la izquierda del todo
            }
        }
    }
    
    
    /**
     * Genera un panel visual (Dashboard) con el resumen estadístico de la actividad.
     * Muestra la distancia total, el tiempo empleado y la velocidad media en una caja
     * se acopla dinámicamente debajo de la gráfica de desnivel.
     * * [Código asistido por IA]
     * * @param tiempo      Cadena de texto formateada (HH:MM:SS) con la duración total.
     * @param distanciaKm Distancia total recorrida en kilómetros.
     * @param velocidad   Velocidad media de la actividad en km/h.
     */
    public void mostrarEstadisticasEnPantalla(String tiempo, double distanciaKm, double velocidad) {
        
        javafx.scene.layout.VBox statsBox = new javafx.scene.layout.VBox(5);
        statsBox.setAlignment(javafx.geometry.Pos.CENTER); 
        statsBox.setStyle("-fx-padding: 10px; -fx-background-color: #fdfdfd; -fx-border-color: #e6e6e6; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-margin: 10px;");

        javafx.scene.control.Label titulo = new javafx.scene.control.Label("Resumen del Track");
        titulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        javafx.scene.control.Label lblDist = new javafx.scene.control.Label(String.format("Distancia: %.2f km", distanciaKm));
        lblDist.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        javafx.scene.control.Label lblTiempo = new javafx.scene.control.Label("Tiempo: " + tiempo);
        lblTiempo.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        javafx.scene.control.Label lblVel = new javafx.scene.control.Label(String.format("Velocidad Media: %.2f km/h", velocidad));
        lblVel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        statsBox.getChildren().addAll(titulo, lblDist, lblTiempo, lblVel);

        if (graficaDesnivel.getParent() instanceof Pane) {
            Pane padre = (Pane) graficaDesnivel.getParent();

            if(padre.getChildren().size() < 3) {
                 padre.getChildren().add(statsBox); // Lo ponemos a la derecha del todo
            } else {
                 padre.getChildren().set(2, statsBox);
            }
        }
    }
    
}