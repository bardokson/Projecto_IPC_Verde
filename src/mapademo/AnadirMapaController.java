package mapademo;

import java.io.File;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

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
            double latMin = Double.parseDouble(latMinField.getText());
            double latMax = Double.parseDouble(latMaxField.getText());
            double lonMin = Double.parseDouble(lonMinField.getText());
            double lonMax = Double.parseDouble(lonMaxField.getText());

            String nombreMapa = imagenSeleccionada.getName().replaceFirst("[.][^.]+$", "");

            SportActivityApp app = SportActivityApp.getInstance();
            
            MapRegion nuevaRegion = app.addMapRegion(nombreMapa, imagenSeleccionada, latMin, latMax, lonMin, lonMax);

            if (nuevaRegion != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El mapa se ha añadido correctamente a la base de datos.");
                
                // Cierra la ventana actual tras guardar
                
                javafx.scene.Node source = (javafx.scene.Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
                
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "La librería no pudo añadir el mapa. Revisa las coordenadas.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de formato", "Las coordenadas deben ser números válidos (usa un punto para los decimales).");
        }
    }

    
    
    

    // Método de apoyo para mostrar ventanas emergentes 
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    
    @FXML
    private void accCancelar(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar cancelación");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Estás seguro de que deseas cancelar? Los datos no guardados se perderán.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Obtiene la referencia a la ventana actual a través del evento y la cierra
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }
    
    
    
    @FXML
    public void initialize() {
        configurarFiltroNumerico(latMinField);
        configurarFiltroNumerico(latMaxField);
        configurarFiltroNumerico(lonMinField);
        configurarFiltroNumerico(lonMaxField);
    }

    /*
     * permitir unicamente la entrada de caracteres numericos, el punto decimal y el signo negativo.
     */
    private void configurarFiltroNumerico(TextField campoTexto) {
        campoTexto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^-?\\d*(\\.\\d*)?$")) {
                campoTexto.setText(oldValue);
            }
        });
    }
}