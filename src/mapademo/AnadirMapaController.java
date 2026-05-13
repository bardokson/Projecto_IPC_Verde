package mapademo;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
// Importamos las clases de la librería de la práctica
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;

public class AnadirMapaController {

    // Las variables inyectadas desde el Scene Builder (fx:id)
    @FXML private TextField rutaImagenField;
    @FXML private TextField latMinField;
    @FXML private TextField latMaxField;
    @FXML private TextField lonMinField;
    @FXML private TextField lonMaxField;

    // Guardaremos el archivo de imagen que seleccione el usuario
    private File imagenSeleccionada;

    @FXML
    void seleccionarImagen(ActionEvent event) {
        // Abrimos el explorador de archivos
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar mapa JPG");
        // Filtramos para que solo deje elegir archivos .jpg
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg", "*.jpeg"));
        
        // Mostramos la ventana
        File file = fc.showOpenDialog(rutaImagenField.getScene().getWindow());
        
        if (file != null) {
            imagenSeleccionada = file;
            // Escribimos la ruta en el cajón de texto para que el usuario la vea
            rutaImagenField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void guardarMapa(ActionEvent event) {
        if (imagenSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debes seleccionar una imagen para el mapa.");
            return;
        }

        try {
            // Leemos los textos de los cajones y los convertimos a números decimales (double)
            double latMin = Double.parseDouble(latMinField.getText());
            double latMax = Double.parseDouble(latMaxField.getText());
            double lonMin = Double.parseDouble(lonMinField.getText());
            double lonMax = Double.parseDouble(lonMaxField.getText());

            // Sacamos el nombre del archivo sin la extensión .jpg para usarlo como nombre de la región
            String nombreMapa = imagenSeleccionada.getName().replaceFirst("[.][^.]+$", "");

            // Obtenemos la instancia de la base de datos de la librería
            SportActivityApp app = SportActivityApp.getInstance();
            
            // Registramos la nueva región en el sistema
            MapRegion nuevaRegion = app.addMapRegion(nombreMapa, imagenSeleccionada, latMin, latMax, lonMin, lonMax);

            if (nuevaRegion != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El mapa se ha añadido correctamente a la base de datos.");
                cerrarFormulario(null); // Limpiamos todo
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "La librería no pudo añadir el mapa. Revisa las coordenadas.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de formato", "Las coordenadas deben ser números válidos (usa un punto para los decimales).");
        }
    }

    @FXML
    void cerrarFormulario(ActionEvent event) {
        // Limpiamos todos los campos para dejar el formulario como nuevo
        rutaImagenField.clear();
        latMinField.clear();
        latMaxField.clear();
        lonMinField.clear();
        lonMaxField.clear();
        imagenSeleccionada = null;
    }

    // Método de apoyo para mostrar ventanitas emergentes bonitas
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}