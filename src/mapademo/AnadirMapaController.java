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
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;

public class AnadirMapaController {

    @FXML private TextField rutaImagenField;
    @FXML private TextField latMinField;
    @FXML private TextField latMaxField;
    @FXML private TextField lonMinField;
    @FXML private TextField lonMaxField;    
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    
    private File imagenSeleccionada;

    /**
     * Permite al usuario seleccionar un mapa para ser usado
     * @param event 
     */
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
    
    /**
     * Guarda el mapa para poder ser seleccionado despues
     * @param event 
     */
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

            if (latMin >= latMax || lonMin >= lonMax) {
                mostrarAlerta(Alert.AlertType.WARNING, "Coordenadas inválidas", "Las coordenadas máximas deben ser estrictamente mayores que las mínimas.");
                return;
            }
            

            String nombreMapa = imagenSeleccionada.getName().replaceFirst("[.][^.]+$", "");

            SportActivityApp app = SportActivityApp.getInstance();
            
            MapRegion nuevaRegion = app.addMapRegion(nombreMapa, imagenSeleccionada, latMin, latMax, lonMin, lonMax);

            if (nuevaRegion != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El mapa se ha añadido correctamente a la base de datos.");

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

    /**
     * Metodo que genera las alertas.
     * IA
     * 
     * @param tipo tipo de alerta
     * @param titulo titulo de la alerta
     * @param mensaje mensaje de la alerta
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Devuelve a la ventana de actividades.
     * IA
     * 
     * @param event 
     */
    @FXML
    private void accCancelar(ActionEvent event) {
        
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar cancelación");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Estás seguro de que deseas cancelar? Los datos no guardados se perderán.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }
    
    /**
     * Inicializador del controlador para añadir mapas.
     */
    @FXML
    public void initialize() {
        configurarFiltroNumerico(latMinField);
        configurarFiltroNumerico(latMaxField);
        configurarFiltroNumerico(lonMinField);
        configurarFiltroNumerico(lonMaxField);
    }

    /**
     * Permitir unicamente la entrada de caracteres numericos, el punto decimal y el signo negativo.
     * 
     * @param campoTexto campo de texto que configurar
     */
    private void configurarFiltroNumerico(TextField campoTexto) {
        campoTexto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^-?\\d*(\\.\\d*)?$")) {
                campoTexto.setText(oldValue);
            }
        });
    }
}