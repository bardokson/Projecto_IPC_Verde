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
    public void initialize() {
        
        // Como es solo una leyenda informativa, no se necesita lógica extra de botones.
    }

   
    
    
    
    
    public Group generarTrazadoVelocidad(Activity actividad, MapProjection proj) {
        Group trazadoColores = new Group();

        if (actividad == null || actividad.getTrackPoints().isEmpty()) {
            return trazadoColores;
        }

        List<TrackPoint> puntos = actividad.getTrackPoints();

        // Iteramos desde el primer punto hasta el PENÚLTIMO
        for (int i = 0; i < puntos.size() - 1; i++) {
            TrackPoint p1 = puntos.get(i);
            TrackPoint p2 = puntos.get(i + 1);

            // 1. Calcular la velocidad entre estos dos puntos
            // speedTo devuelve la velocidad en km/h
            double velocidad = p1.speedTo(p2);

            // 2. Proyectar las coordenadas GPS a píxeles de la pantalla
            Point2D pix1 = proj.project(p1);
            Point2D pix2 = proj.project(p2);

            // 3. Crear el segmento de línea
            Line tramo = new Line(pix1.getX(), pix1.getY(), pix2.getX(), pix2.getY());
            tramo.setStrokeWidth(4.0); // Grosor de la línea
            tramo.setStrokeLineCap(StrokeLineCap.ROUND); // Bordes redondeados para que quede suave
            
            // 4. Decidir el color en función de la velocidad
            if (velocidad < 8.0) {
                tramo.setStroke(Color.RED);        // Lento
            } else if (velocidad >= 8.0 && velocidad <= 12.0) {
                tramo.setStroke(Color.ORANGE);     // Medio
            } else {
                tramo.setStroke(Color.LIMEGREEN);  // Rápido
            }

            // Añadir tramo al grupo
            trazadoColores.getChildren().add(tramo);
        }

        return trazadoColores;
    }
}
