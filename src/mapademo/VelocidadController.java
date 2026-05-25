/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.MapProjection;
import upv.ipc.sportlib.TrackPoint;

public class VelocidadController {

    @FXML
    public void initialize() {}

    /**
     * Genera el trazado de velocidad de una actividad sobre una projeccion.
     * IA
     * 
     * @param actividad La actividad de que que se genera el trazado
     * @param proj El mapa donde se projecta el trazado
     * @return 
     */
    public Group generarTrazadoVelocidad(Activity actividad, MapProjection proj) {
        
        Group trazadoColores = new Group();

        if (actividad == null || actividad.getTrackPoints().isEmpty()) {
            return trazadoColores;
        }

        List<TrackPoint> puntos = actividad.getTrackPoints();

        for (int i = 0; i < puntos.size() - 1; i++) {
            TrackPoint p1 = puntos.get(i);
            TrackPoint p2 = puntos.get(i + 1);

            double velocidad = p1.speedTo(p2);

            Point2D pix1 = proj.project(p1);
            Point2D pix2 = proj.project(p2);

            Line tramo = new Line(pix1.getX(), pix1.getY(), pix2.getX(), pix2.getY());
            tramo.setStrokeWidth(4.0);
            tramo.setStrokeLineCap(StrokeLineCap.ROUND);

            if (velocidad < 8.0) {
                tramo.setStroke(Color.web("#2c3e50"));   // Azul marino - Lento
            } else if (velocidad >= 8.0 && velocidad <= 12.0) {
                tramo.setStroke(Color.web("#7f8c8d"));   // Gris - Medio
            } else {
                tramo.setStroke(Color.web("#27ae60"));   // Verde - Rapido
            }

            trazadoColores.getChildren().add(tramo);
        }

        javafx.scene.text.Text leyendaRoja = new javafx.scene.text.Text(10, 20, "▬ < 8 km/h (Lento)");
        leyendaRoja.setFill(Color.web("#2c3e50"));
        leyendaRoja.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        javafx.scene.text.Text leyendaNaranja = new javafx.scene.text.Text(10, 40, "▬ 8-12 km/h (Medio)");
        leyendaNaranja.setFill(Color.web("#7f8c8d"));
        leyendaNaranja.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        javafx.scene.text.Text leyendaVerde = new javafx.scene.text.Text(10, 60, "▬ > 12 km/h (Rapido)");
        leyendaVerde.setFill(Color.web("#27ae60"));
        leyendaVerde.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        trazadoColores.getChildren().addAll(leyendaRoja, leyendaNaranja, leyendaVerde);
        return trazadoColores;
    }
    
}




