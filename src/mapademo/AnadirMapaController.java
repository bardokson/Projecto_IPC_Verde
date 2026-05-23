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



/**
 * Controlador encargado de la lógica de la ventana emergente "Añadir Nuevo Mapa".
 * Gestiona la selección del archivo físico (JPG) y la validación de las coordenadas
 * del Bounding Box introducidas por el usuario antes de inyectarlas en el sistema.
 */


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
     * Abre el explorador de archivos nativo para que el usuario seleccione el mapa físico.
     * Una vez seleccionado, muestra la ruta absoluta del archivo en el campo de texto correspondiente.
     *
     * @param event Evento disparado por la pulsación del botón "Examinar...".
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
     * Procesa, valida y guarda la información del nuevo mapa en el sistema.
     * [Código asistido por IA]
     * @param event Evento disparado por la pulsación del botón "Guardar Mapa".
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
     * Método auxiliar para generar y mostrar alertas en pantalla.
     *
     * @param tipo    El tipo de alerta (ERROR, WARNING, INFORMATION, CONFIRMATION).
     * @param titulo  El título superior de la ventana de alerta.
     * @param mensaje El texto que leerá el usuario.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Intercepta cancelar la subida del mapa y pide confirmación al usuario.
     * Evita la pérdida accidental de datos 
     * Si el usuario confirma, obtiene la referencia del Stage actual y destruye la ventana.
     * 
     * [Código asistido por IA]
     *
     * @param event Evento disparado por la pulsación del botón "Cancelar".
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
     * Aplica un listener reactivo a un campo de texto para restringir su entrada en tiempo real.
     * 
     * [Código asistido por IA]
     * 
     * @param campoTexto El TextField sobre el que se aplicará el filtro de validación.
     */
    private void configurarFiltroNumerico(TextField campoTexto) {
        campoTexto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^-?\\d*(\\.\\d*)?$")) {
                campoTexto.setText(oldValue);
            }
        });
    }
}

