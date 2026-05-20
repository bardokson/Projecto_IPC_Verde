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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.TrackPoint;
import upv.ipc.sportlib.GeoUtils;
import upv.ipc.sportlib.MapProjection;

public class DesnivelController {

    @FXML private AreaChart<Number, Number> graficaDesnivel;
    @FXML private NumberAxis ejeX;
    @FXML private NumberAxis ejeY;

    private Activity actividadActual;
    private List<Double> distanciasAcumuladas; // Guardar la distancia de cada punto

    // Variables para comunicar con mapa principal
    private Pane mapPane;
    private MapProjection projection;
    private Circle puntoRastreador; // El punto flotante que viaja por el mapa
    private javafx.scene.text.Text textoInfoRastreador; // El texto que acompañará a la bolita
    
    
    /**
     * Inicializa el controlador para mostrar el desnivel de una ruta
     */
    public void initialize() {
        graficaDesnivel.setLegendVisible(false); 
        graficaDesnivel.setCreateSymbols(false); 
        graficaDesnivel.setAnimated(false); 

        // Hacemos que el eje Y se ajuste a las altitudes reales 
        // y no nos obligue a empezar siempre desde 0 metros.
        ejeY.setForceZeroInRange(false);

        // Escuchamos el movimiento del ratón sobre nuestra gráfica
        graficaDesnivel.setOnMouseMoved(this::onMouseMovedGraph);
        
        // Esconder el punto y texto si sacas el ratón de la gráfica
        graficaDesnivel.setOnMouseExited(e -> {
            if (puntoRastreador != null) puntoRastreador.setVisible(false);
            if (textoInfoRastreador != null) textoInfoRastreador.setVisible(false);
        });
    }

    public void setMapContext(Pane mapPane, MapProjection projection) {
        this.mapPane = mapPane;
        this.projection = projection;

        // Creamos punto rastreador
        this.puntoRastreador = new Circle(6, Color.BLUE);
        this.puntoRastreador.setStroke(Color.WHITE);
        this.puntoRastreador.setStrokeWidth(2);
        this.puntoRastreador.setMouseTransparent(true); // Para que no bloquee clics
        this.puntoRastreador.setVisible(false);
        
        // NUEVA MEJORA: Inicializamos nuestro texto flotante
        this.textoInfoRastreador = new javafx.scene.text.Text();
        this.textoInfoRastreador.setStyle("-fx-font-weight: bold; -fx-fill: #191970;"); // Color azul oscuro
        this.textoInfoRastreador.setVisible(false);
        this.textoInfoRastreador.setMouseTransparent(true);
        
        // Añadimos la bolita y el texto al lienzo del mapa
        this.mapPane.getChildren().addAll(puntoRastreador, textoInfoRastreador);
    }

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
            
            // Guardamos esto para que el ratón sepa qué buscar luego
            distanciasAcumuladas.add(distanciaKm);

            double altitud = puntoActual.getElevation();
            XYChart.Data<Number, Number> datoGrafica = new XYChart.Data<>(distanciaKm, altitud);
            serieDesnivel.getData().add(datoGrafica);

            puntoAnterior = puntoActual;
        }

        graficaDesnivel.getData().add(serieDesnivel);
    }

    // Sincronizar el ratón con el mapa
    // Sincronizar el ratón con el mapa
    private void onMouseMovedGraph(MouseEvent event) {
        if (actividadActual == null || mapPane == null || projection == null || puntoRastreador == null) {
            return;
        }

        // qué coordenada X (kilómetros) tenemos el ratón
        double xEnPixels = ejeX.sceneToLocal(event.getSceneX(), event.getSceneY()).getX();
        Number distanciaKmObj = ejeX.getValueForDisplay(xEnPixels);
        if (distanciaKmObj == null) return;
        double distanciaRaton = distanciaKmObj.doubleValue();

        // NUEVO FIX: Evitar que salga la bolita si estamos en el margen izquierdo (< 0) 
        // o si nos salimos por la derecha de la gráfica.
        if (distanciaRaton < 0 || distanciasAcumuladas.isEmpty() || distanciaRaton > distanciasAcumuladas.get(distanciasAcumuladas.size() - 1)) {
            puntoRastreador.setVisible(false);
            if (textoInfoRastreador != null) textoInfoRastreador.setVisible(false);
            return; // Cortamos la ejecución aquí
        }

        // 2. Buscar en la lista qué punto GPS está más cerca de esa distancia
        int indiceMasCercano = 0;
        double diferenciaMinima = Double.MAX_VALUE;

        for (int i = 0; i < distanciasAcumuladas.size(); i++) {
            double dif = Math.abs(distanciasAcumuladas.get(i) - distanciaRaton);
            if (dif < diferenciaMinima) {
                diferenciaMinima = dif;
                indiceMasCercano = i;
            }
        }

        // 3. Obtener el punto GPS real, traducirlo a píxeles y mover la bolita azul
        TrackPoint puntoGPS = actividadActual.getTrackPoints().get(indiceMasCercano);
        Point2D pixelPos = projection.project(puntoGPS);

        puntoRastreador.setCenterX(pixelPos.getX());
        puntoRastreador.setCenterY(pixelPos.getY());
        puntoRastreador.setVisible(true);
        puntoRastreador.toFront(); // Poner siempre encima de todo
        
        // Movemos el texto junto a la bolita y le ponemos los datos
        textoInfoRastreador.setText(String.format("%.2f km | %.0f m", distanciaRaton, puntoGPS.getElevation()));
        // Lo ponemos un poco desplazado para que el ratón o la bolita no lo tapen
        textoInfoRastreador.setX(pixelPos.getX() + 12);
        textoInfoRastreador.setY(pixelPos.getY() - 12);
        textoInfoRastreador.setVisible(true);
        textoInfoRastreador.toFront();
    }

    // =========================================================
    // LEYENDA DE VELOCIDAD
    // =========================================================
    public void crearLeyendaVelocidad() {
        // Creamos un contenedor horizontal para los textos
        javafx.scene.layout.HBox leyenda = new javafx.scene.layout.HBox(15);
        leyenda.setAlignment(javafx.geometry.Pos.CENTER);
        leyenda.setPadding(new javafx.geometry.Insets(0, 0, 10, 0)); // Margen inferior

        // Usamos los colores y rangos exactos que tus compañeros han puesto en el main
        javafx.scene.control.Label l1 = new javafx.scene.control.Label("▬ Lento (< 35)");
        l1.setTextFill(Color.RED);
        l1.setStyle("-fx-font-weight: bold;");

        javafx.scene.control.Label l2 = new javafx.scene.control.Label("▬ Medio (35-40)");
        l2.setTextFill(Color.ORANGE);
        l2.setStyle("-fx-font-weight: bold;");

        javafx.scene.control.Label l3 = new javafx.scene.control.Label("▬ Rápido (> 40)");
        l3.setTextFill(Color.LIMEGREEN);
        l3.setStyle("-fx-font-weight: bold;");

        leyenda.getChildren().addAll(l1, l2, l3);

        // Cogemos el VBox principal de esta vista (el padre de la gráfica)
        if (graficaDesnivel.getParent() instanceof javafx.scene.layout.VBox) {
            javafx.scene.layout.VBox padre = (javafx.scene.layout.VBox) graficaDesnivel.getParent();
            
            // FIX: Lo insertamos en la posición 0 (arriba del todo, por encima del título)
            if (padre.getChildren().size() == 2) {
                padre.getChildren().add(0, leyenda);
            }
        }
    
    }
}