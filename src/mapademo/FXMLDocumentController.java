/*
 * ============================================================
 *  PROYECTO EJEMPLO – IPC 2026
 *  Asignatura: Interfaces Persona-Computador
 *  Universitat Politècnica de València
 * ============================================================
 *
 *  DESCRIPCIÓN GENERAL
 *  -------------------
 *  Este controlador gestiona la vista principal de la aplicación
 *  de puntos de interés (POI) sobre un mapa.
 *
 *  Funcionalidades implementadas:
 *   1. Carga y visualización de una imagen de mapa.
 *   2. Zoom interactivo mediante un Slider.
 *   3. Añadir POIs (texto) y anotaciones (círculos) con clic derecho.
 *   4. Listado de POIs en un ListView con CellFactory personalizada.
 *   5. Centrado animado del mapa al seleccionar un POI de la lista.
 *   6. Modo inserción: activar con botón y colocar POI con siguiente clic.
 *
 *  PATRÓN UTILIZADO: MVC (Model-View-Controller)
 *   - Modelo : clase Poi  (datos del punto de interés)
 *   - Vista  : FXMLDocument.fxml  (layout declarativo)
 *   - Control: esta clase (lógica de interacción)
 *
 * ============================================================
 */
package mapademo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.Annotation;
import upv.ipc.sportlib.GeoPoint;
import upv.ipc.sportlib.AnnotationType;
import static upv.ipc.sportlib.AnnotationType.CIRCLE;
import static upv.ipc.sportlib.AnnotationType.LINE;
import static upv.ipc.sportlib.AnnotationType.POINT;
import static upv.ipc.sportlib.AnnotationType.TEXT;
import upv.ipc.sportlib.MapProjection;  
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.TrackPoint;
import upv.ipc.sportlib.User;

/**
 * Controlador principal de la aplicación LaSaforApp.
 *
 * La anotación @FXML conecta automáticamente los campos de esta clase
 * con los elementos declarados en el fichero FXML mediante su atributo fx:id.
 *
 * Implementa {@link Initializable} para poder ejecutar código de
 * inicialización una vez que el FXML ha sido cargado completamente.
 */
public class FXMLDocumentController implements Initializable {

    private Pane mapPane;
    @FXML private ListView<Annotation> map_listview;
    @FXML private ScrollPane map_scrollpane;
    @FXML private Slider zoom_slider;
    @FXML private Label mousePosition;
    @FXML private SplitPane splitPaneMapa;
    @FXML private ListView<Activity> activityList;
    @FXML private ImageView userAvatar;
    @FXML private GridPane gridBase;
    @FXML private Menu username;
    @FXML private HBox modActivity;
    @FXML private HBox modNotes;
    @FXML private MenuItem mod;
    @FXML private MenuItem ests;
    @FXML private MenuItem ses;
    @FXML private MenuItem out;
    @FXML private MenuItem auth;
    @FXML private Menu men;
    @FXML private Button imp;
    @FXML private HBox imph;
    
    private static boolean guest = false;

    
    private static List<String> mapas = new ArrayList<>();
    public static ObservableList<String> mapaOb = FXCollections.observableList(mapas);
    
    private int circleCounter = 1;
    private int PoiCounter = 1;
    private int LineCounter = 1;
    private int PointCounter = 1;
    private double lineX, lineY;
    private final java.util.Set<Long> anotacionesBorradas = new java.util.HashSet<>();
    private final java.util.Map<Long, java.util.List<Annotation>> anotacionesPorActividad = new java.util.HashMap<>();
    private boolean insertionMode = false;
    private boolean lineInput = false;
    private Group zoomGroup;
    private Activity actividadActual;
    private MapProjection projection;
    private ContextMenu mapContextMenu;
    private User user;
    private String lineName;
    private Color lineColor;
    private Long actividadActualId = null;
    private String nombre;
    private static File imgFileCam;
    
    /**
     * Pone al usuario como invitado o no.
     * 
     * @param is
     * 
     * @author Erik Tzvetkov
     */
    public static void setGuest(boolean is){guest = is;}
    
    /**
     * Devuelve si el usuario es invitado.
     * 
     * @author Erik Tzvetkov
     */
    private static boolean isGuest(){return guest;}
    /**
     * Aumenta el zoom en 0.1 unidades al pulsar el botón "+".
     *
     * @param event evento de acción del botón
     */
    @FXML
    void zoomIn(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + 0.1);
    }

    /**
     * Reduce el zoom en 0.1 unidades al pulsar el botón "–".
     *
     * @param event evento de acción del botón
     */
    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal - 0.1);
    }

    /**
     * Aplica el factor de escala al {@code zoomGroup}.
     *
     * Este método es invocado automáticamente cada vez que cambia el
     * valor del slider, gracias al listener registrado en {@link #initialize}.
     *
     * Truco: guardamos y restauramos los valores de scroll para que el
     * contenido visible no salte al cambiar la escala.
     *
     * @param scaleValue nuevo factor de escala (p. ej. 1.2 → 120 %)
     */
    private void zoom(double scaleValue) {
        // Guardamos la posición del scroll antes de escalar
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();

        // Aplicamos el zoom escalando el Group en ambos ejes
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        // Restauramos la posición del scroll para que el centro visual
        // permanezca estable durante el zoom
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }
    
    /**
     * Permite controlar el zoom combinando CTRL y mousewheel.
     * 
     * @param event 
     * 
     * @author Hector Saez
    */
    private void ZoomCtrl(ScrollEvent event) {
        if (event.isControlDown()) {
            double valorActual = zoom_slider.getValue();
            double sensibilidad = 0.05; 
            if (event.getDeltaY() > 0) {
                zoom_slider.setValue(Math.min(zoom_slider.getMax(), valorActual + sensibilidad));
            } else if (event.getDeltaY() < 0) {
                zoom_slider.setValue(Math.max(zoom_slider.getMin(), valorActual - sensibilidad));
            }
            event.consume();
        }
    }

    // =========================================================
    //  SELECCIÓN EN EL LISTVIEW → CENTRADO EN EL MAPA
    // =========================================================

    /**
     * Se ejecuta cuando el usuario hace clic en un elemento del ListView.
     *
     * Modificado del metodo original. Adaptado para usar anotaciones en general, no solo POIs.
     *
     * @param event evento de ratón sobre el ListView
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    @FXML
    void listClicked(MouseEvent event) {
        Annotation note = map_listview.getSelectionModel().getSelectedItem();
        if (note == null) return;

        Point2D p = projection.project(note.getGeoPoints().get(0));
        double x = p.getX();
        double y = p.getY();

        mapPane.layout();

        Platform.runLater(() -> centrarEnPunto(x, y));
    }

    // =========================================================
    //  CONSTRUCCIÓN DEL MAPA
    // =========================================================

    /**
     * Carga una imagen y construye la jerarquía de nodos del mapa.
     *
     * Este método puede llamarse varias veces (p. ej. al cambiar el mapa),
     * ya que sustituye completamente el contenido del ScrollPane.
     *
     * @param imgFile fichero de imagen a cargar como fondo del mapa
     */
    private void buildMap(File imgFile, upv.ipc.sportlib.MapRegion region) {
        // Comprobación defensiva: si el fichero no existe mostramos un aviso
        if (!imgFile.exists()) {
            map_scrollpane.setContent(
                new Label("Imagen no encontrada: " + imgFile.getPath()));
            return;
        }

        // Cargamos la imagen y obtenemos sus dimensiones reales en píxeles
        Image img = new Image(imgFile.toURI().toString());
        double W = img.getWidth();
        double H = img.getHeight();
        
        // FIX: Inicializar la proyección para poder transformar GPS a píxeles
        if (region != null) {
            projection = new upv.ipc.sportlib.MapProjection(region, W, H);
        }

        // ── mapPane: lienzo del mapa ───────────────────────────────────
        // Usamos un Pane (y no un Group) para poder posicionar los nodos
        // hijos con coordenadas absolutas (setLayoutX / setLayoutY).
        mapPane = new Pane();
        mapPane.setPrefSize(W, H); // tamaño preferido = tamaño de la imagen
        mapPane.setMinSize(W, H);  // impedimos que el layout lo encoja
        mapPane.setMaxSize(W, H);  // impedimos que el layout lo agrande

        // Añadimos la imagen como fondo del Pane
        ImageView iv = new ImageView(img);
        iv.setFitWidth(W);
        iv.setFitHeight(H);
        mapPane.getChildren().add(iv);

        // ── Manejador de clics sobre el mapa ──────────────────────────
        // Gestionamos el clic derecho (menú contextual) y el clic izquierdo
        // en modo inserción (FIX 2).
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // Clic derecho → mostrar menú contextual
                onMapRightClick(e.getX(), e.getY());

            } else if (e.getButton() == MouseButton.PRIMARY && insertionMode) {
                // FIX 2: clic izquierdo en modo inserción → añadir POI y desactivar modo
                insertionMode = false;
                mapPane.setStyle(""); // Restauramos el cursor normal
                addPoi(e.getX(), e.getY());
            }
        });

        // ── Jerarquía de Groups para el zoom ──────────────────────────
        // contentGroup es el nodo raíz que recibe el ScrollPane.
        // zoomGroup es el que se escala; anidar un Group dentro de otro
        // evita que el ScrollPane reajuste su contenido durante el escalado.
        zoomGroup = new Group();
        Group contentGroup = new Group();
        zoomGroup.getChildren().add(mapPane);
        contentGroup.getChildren().add(zoomGroup);

        // Aplicamos el zoom actual (valor actual del slider)
        double zoom = zoom_slider.getValue();
        zoomGroup.setScaleX(zoom);
        zoomGroup.setScaleY(zoom);

        // Asignamos el contentGroup como contenido del ScrollPane
        map_scrollpane.setContent(contentGroup);

    }

    // =========================================================
    //  MENÚ CONTEXTUAL (clic derecho sobre el mapa)
    // =========================================================

    /**
     * Muestra el menú contextual reutilizable en la posición del clic.
     *
     * Modificado del metodo original para permitir mas tipos de anotaciones
     *
     * @param x coordenada X del clic en el sistema local del mapPane
     * @param y coordenada Y del clic en el sistema local del mapPane
     * 
     * @author Jiaxiang Liu
     */
    private void onMapRightClick(double x, double y) {
        if(!isGuest()){
        // FIX 6: cerramos el menú si ya estaba visible (evita instancias flotantes)
        mapContextMenu.hide();
        
        // Actualizamos las acciones de los items con las coordenadas actuales.
        // Usamos variables final para que el lambda pueda capturarlas.
        final double clickX = x;
        final double clickY = y;
        mapContextMenu.getItems().get(0).setOnAction(e -> addPoi(clickX, clickY));
        mapContextMenu.getItems().get(1).setOnAction(e -> addCircle(clickX, clickY));
        mapContextMenu.getItems().get(2).setOnAction(e -> addLine(clickX, clickY));
        mapContextMenu.getItems().get(3).setOnAction(e -> addPoint(clickX, clickY));

        mapContextMenu.show(
            mapPane.getScene().getWindow(),
            mapPane.localToScreen(x, y).getX(),
            mapPane.localToScreen(x, y).getY()
        );
        }
    }

    // =========================================================
    //  INICIALIZACIÓN DEL CONTROLADOR
    // =========================================================

    /**
     * Método llamado automáticamente por el FXMLLoader tras inyectar
     * todos los elementos {@code @FXML}.
     *
     * Aquí configuramos:
     *  - El slider de zoom y su listener.
     *  - El ContextMenu reutilizable (FIX 6).
     *  - La CellFactory del ListView (FIX 4).
     *  - La carga del mapa inicial.
     *
     * @param url  URL del documento FXML (no usado aquí)
     * @param rb   paquete de recursos de internacionalización (no usado aquí)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(isGuest()){
            PopOver popover = new PopOver(new Label("  Para poder importar una actividad o mapa \n " 
                    + "      debes de iniciar sesion o registrarte  \n"));
            activityList.setDisable(isGuest());
            map_listview.setDisable(isGuest());
            username.setText("Invitado");
            mod.setVisible(!isGuest());
            ests.setVisible(!isGuest());
            ses.setVisible(!isGuest());
            out.setVisible(!isGuest());
            auth.setVisible(isGuest());
            men.setDisable(isGuest());
            imp.setDisable(isGuest());
            imph.setOnMouseEntered(e-> popover.show(imph));
        }

        zoom_slider.setMin(0.5);   
        zoom_slider.setMax(1.5);   
        zoom_slider.setValue(1.0);
        // Listener que invoca zoom() cada vez que el slider cambia de valor.
        // Usamos una expresión lambda en lugar de una clase anónima por brevedad.
        zoom_slider.valueProperty().addListener(
            (observable, oldVal, newVal) -> zoom((Double) newVal)
        );

        // Los items se crean aquí sin acción; las acciones se asignan
        // en onMapRightClick() con las coordenadas correctas de cada clic.
        MenuItem miText = new MenuItem("📝 Añadir texto");
        MenuItem miCircle = new MenuItem("⭕ Añadir círculo");
        MenuItem miPoint = new MenuItem("📍 Añadir punto");
        MenuItem miLine = new MenuItem("📏 Añadir linea");
        mapContextMenu = new ContextMenu(miText, miCircle, miLine, miPoint);

        //Modificado del original para permitir todas las anotaciones y solo el nombre
        map_listview.setCellFactory(listView -> new ListCell<Annotation>() {
            @Override
            protected void updateItem(Annotation note, boolean empty) {
                super.updateItem(note, empty);

                if (empty || note == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(note.getText());
                }
            }
        });

        File archivoMapa = new File("maps/upv.jpg");
        buildMap(archivoMapa, null);
        if(!isGuest()){
        setupUser();
        verActividades();
        }
        
        modActivity.disableProperty().bind(activityList.getSelectionModel().selectedItemProperty().isNull());
        modNotes.disableProperty().bind(map_listview.getSelectionModel().selectedItemProperty().isNull());
       
        map_scrollpane.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() == 0) return;

            if (event.isControlDown()) {
                double paso = 0.05; 
                double valorActual = zoom_slider.getValue();

                if (event.getDeltaY() > 0) {
                    double nuevoValor = Math.min(zoom_slider.getMax(), valorActual + paso);
                    zoom_slider.setValue(nuevoValor);
                } else {
                    double nuevoValor = Math.max(zoom_slider.getMin(), valorActual - paso);
                    zoom_slider.setValue(nuevoValor);
                }
                event.consume();
            }
        });
        if(mapas.isEmpty()) {
        mapas.add("maps/upv.jpg");
        mapas.add("maps/calderona.jpg");
        mapas.add("maps/pirineos.jpg"); 
        mapas.add("maps/valencia.jpg");
        }
    }

    // =========================================================
    //  INDICADOR DE POSICIÓN DEL RATÓN
    // =========================================================

    /**
     * Actualiza la etiqueta {@code mousePosition} con las coordenadas
     * actuales del ratón, tanto en el sistema de la escena como en el
     * sistema local del nodo sobre el que se mueve.
     *
     * Útil para depuración y para que los alumnos comprendan la diferencia
     * entre coordenadas de escena y coordenadas locales.
     *
     * @param event evento de movimiento del ratón
     */
    @FXML
    private void showPosition(MouseEvent event) {
        mousePosition.setText(
            "sceneX: " + (int) event.getSceneX() +
            ", sceneY: " + (int) event.getSceneY() + "\n" +
            "         X: " + (int) event.getX() +
            ",          Y: " + (int) event.getY()
        );
    }


    // =========================================================
    //  DIÁLOGO "ACERCA DE"
    // =========================================================

    /**
     * Muestra un diálogo informativo con datos de la asignatura.
     *
     * Nota: accedemos al Stage del diálogo para poder personalizar
     * su icono, ya que Alert no expone directamente esa propiedad.
     *
     * @param event evento de acción del menú
     * 
     * @author Jiaxiang Liu
     */
    @FXML
    private void about(ActionEvent event) {
        Alert mensaje = new Alert(Alert.AlertType.INFORMATION);

        // Personalizamos el icono de la ventana del diálogo
        Stage dialogStage = (Stage) mensaje.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(
            new Image(getClass().getResourceAsStream("/resources/logo.png"))
        );

        mensaje.setTitle("Acerca de");
        mensaje.setHeaderText("IPC - 2026");
        mensaje.setContentText("""
                               Autores del programa:
                                Jiaxiang Liu Shan
                                Erik Tzvetkov Radev
                                Hector Saez Montero
                                Javier Blanch Rozalen
                               """);
        mensaje.showAndWait(); // Bloquea hasta que el usuario cierra el diálogo
    }

    // =========================================================
    //  AÑADIR UN POI (texto) AL MAPA
    // =========================================================
    
    /**
     * Metodo para transformar de color a hexadecimal.
     * 
     * @param color
     * 
     * @author Hector Saez
     */
    public String colorToHex(Color color) {
        if (color == null) {
            return "#000000";
        }
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    
    /**
     * Metodo para transformar de hexadecimar a color.
     * 
     * @param hex
     * 
     * @autor Hector Saez
     */
    public Color hexToColor(String hex) {
        if (hex == null || hex.isEmpty()) {
            return Color.BLACK; 
        }
        return Color.web(hex);
    }   
    
    /**
     * Muestra un diálogo para introducir el nombre del nuevo POI,
     * lo añade al ListView y dibuja su etiqueta sobre el mapa.
     *
     * @param x coordenada X del clic en el sistema local del mapPane
     * @param y coordenada Y del clic en el sistema local del mapPane
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    private void addPoi(double x, double y) {
        Dialog<ButtonType> poiDialog = new Dialog<>();
        poiDialog.setTitle("Nuevo POI");
        poiDialog.setHeaderText("Introduce un nuevo POI");
        Stage dialogStage = (Stage) poiDialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        poiDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del POI");
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        VBox vbox = new VBox(10, new Label("Nombre:"), nameField, new Label("Color del texto:"), colorPicker);
        poiDialog.getDialogPane().setContent(vbox);

        Optional<ButtonType> result = poiDialog.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            String nombre = nameField.getText().isEmpty() ? "Texto " + PoiCounter++ : nameField.getText();
            GeoPoint geo = projection.unproject(x, y);
            Annotation note = new Annotation(AnnotationType.TEXT, nombre, colorToHex(colorPicker.getValue()), 2.0, List.of(geo));
            Annotation saved = LaSaforApp.app.addAnnotation(actividadActual, note);
            registrarAnotacion(saved);
            drawPoi(saved);
        }
    }
    
    /**
     * Dibuja el texto de un POI en el mapa.
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    private void drawPoi(Annotation note) {
        
        GeoPoint a = note.getGeoPoints().get(0);
        Point2D p = projection.project(a);
        Text text = new Text(note.getText());
        text.setX(p.getX());
        text.setY(p.getY());
        text.setFill(hexToColor(note.getColor()));
        text.setUserData("annotation");
        
        mapPane.getChildren().add(text);
    }


    // =========================================================
    //  CAMBIAR EL MAPA (selector de fichero)
    // =========================================================

    /**
     * Abre un selector de fichero para que el usuario elija una imagen
     * diferente como mapa y reconstruye toda la vista.
     *
     * FIX 3: se comprueba que imgFile no sea null antes de usarlo,
     * evitando NullPointerException cuando el usuario cierra el FileChooser
     * sin seleccionar ningún fichero.
     *
     * @param event evento de acción del menú
     * @throws IOException si hay un problema al obtener la ruta canónica
     */
    @FXML
    private void cambiarMapa(ActionEvent event) throws IOException {
       javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("CambiarMapa.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            
            stage.setTitle("Cambiar mapa");
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/resources/estilos.css");
            stage.setScene(scene);
            stage.initOwner(mapPane.getScene().getWindow());
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.showAndWait();
        if (imgFileCam != null) {
            System.out.println("Mapa seleccionado: " + imgFileCam.getCanonicalPath());
            buildMap(imgFileCam, null); // Reconstruimos la vista con la nueva imagen
            map_listview.getItems().clear(); // Borramos los datos del mapa anterior
        }
    }
    public static void cambiarMapa(String mapa){
        imgFileCam = new File(mapa);
    }
    // =========================================================
    //  AÑADIR UN CÍRCULO AL MAPA
    // =========================================================

    /**
     * Dialogo para añadir una anotacion tipo circulo.
     * 
     * Muestra un dialogo y lo guarda en la base de datos
     * 
     * @param x coordenada X en el sistema local del mapPane
     * @param y coordenada Y en el sistema local del mapPane
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    private void addCircle(double x, double y) {
        
        Dialog<ButtonType> circleDialog = new Dialog<>();
        circleDialog.setTitle("Nuevo circulo");
        circleDialog.setHeaderText("Introduce un nuevo circulo");
        Stage dialogStage = (Stage) circleDialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        circleDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del circulo");
        ColorPicker colorPicker = new ColorPicker(Color.RED);
        VBox vbox = new VBox(10, new Label("Nombre:"), nameField, new Label("Color del círculo:"), colorPicker);
        circleDialog.getDialogPane().setContent(vbox);

        Optional<ButtonType> result = circleDialog.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            String nombre = nameField.getText().isEmpty() ? "Círculo " + circleCounter++ : nameField.getText();
            GeoPoint geo = projection.unproject(x, y);
            Annotation note = new Annotation(AnnotationType.CIRCLE, nombre, colorToHex(colorPicker.getValue()), 2.0, List.of(geo));
            Annotation saved = LaSaforApp.app.addAnnotation(actividadActual, note);
            registrarAnotacion(saved);
            drawCircle(saved);
        }
    }

    /**
     * Dibuja en el mapa la anotacion data de tipo circulo.
     * 
     * @param note anotacion que el metodo dibuja sobre el mapa
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    private void drawCircle(Annotation note) {
        GeoPoint a = note.getGeoPoints().get(0);
        Point2D p = projection.project(a);
        Circle circle = new Circle(10, hexToColor(note.getColor()));
        circle.setCenterX(p.getX());
        circle.setCenterY(p.getY());
        circle.setUserData("annotation");
        mapPane.getChildren().add(circle);
    }
    
    /**
     * Dialogo para añadir una anotacion tipo punto.
     * 
     * Añade un punto a la base de datos
     * 
     * @param x coordenada X en el sistema local del mapPane
     * @param y coordenada Y en el sistema local del mapPane
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    private void addPoint(double x, double y) {
        Dialog<ButtonType> pointDialog = new Dialog<>();
        pointDialog.setTitle("Nuevo punto");
        pointDialog.setHeaderText("Introduce un nuevo punto");
        Stage dialogStage = (Stage) pointDialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        pointDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del punto");
        ColorPicker colorPicker = new ColorPicker(Color.RED);
        VBox vbox = new VBox(10, new Label("Nombre:"), nameField, new Label("Color del punto:"), colorPicker);
        pointDialog.getDialogPane().setContent(vbox);

        Optional<ButtonType> result = pointDialog.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            String nombre = nameField.getText().isEmpty() ? "Punto " + PointCounter++ : nameField.getText();
            GeoPoint geo = projection.unproject(x, y);
            Annotation note = new Annotation(AnnotationType.POINT, nombre, colorToHex(colorPicker.getValue()), 2.0, List.of(geo));
            Annotation saved = LaSaforApp.app.addAnnotation(actividadActual, note);
            registrarAnotacion(saved);
            drawPoint(saved);
        }
    }

    /**
     * Metodo que dibuja anotacion tipo punto sobre el mapa.
     * 
     * @param note anotacion que el metodo dibuja sobre le mapa.
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    private void drawPoint(Annotation note) {
        GeoPoint a = note.getGeoPoints().get(0);
        Point2D p = projection.project(a);
        Circle point = new Circle(5, hexToColor(note.getColor()));
        point.setCenterX(p.getX());
        point.setCenterY(p.getY());
        point.setUserData("annotation");
        mapPane.getChildren().add(point);
    }
    
    /**
     * Dialogo para añadir una anotacion tipo linea.
     * 
     * Usa lineInput para saber si empezar una linea o terminar una linea.
     * Si !lineInput, captura los datos para iniciar la linea.
     * Si lineInput, crea la anotacion linea y la añade a la base de datos.
     * 
     * @param x coordenada X en el sistema local del mapPane
     * @param y coordenada Y en el sistema local del mapPane
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    private void addLine(double x, double y) {
        if (!lineInput) {
            Dialog<ButtonType> lineDialog = new Dialog<>();
            lineDialog.setTitle("Nueva línea");
            lineDialog.setHeaderText("Introduce el nombre y el color de la línea,\nluego haz clic derecho en el punto final.");
 
            ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
            lineDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
 
            TextField nameField = new TextField();
            nameField.setPromptText("Nombre de la línea");
 
            ColorPicker colorPicker = new ColorPicker(Color.RED);
 
            VBox vbox = new VBox(10,
                new Label("Nombre:"), nameField,
                new Label("Color de la línea:"), colorPicker
            );
            lineDialog.getDialogPane().setContent(vbox);
 
            Optional<ButtonType> result = lineDialog.showAndWait();
 
            if (result.isEmpty() || result.get() != okButton) return;
            String nombreIngresado = nameField.getText().trim();
            if (nombreIngresado.isEmpty()) {
                lineName = "Línea " + LineCounter;
                LineCounter++; 
            } else {
                lineName = nombreIngresado; 
            }
            lineColor = colorPicker.getValue();  
            lineInput = true;
            lineX = x;
            lineY = y;
 
        } else {
            drawLine(x, y, lineName, lineColor);
        }
    }

    /**
     * Metodo que crea la anotacion linea y la guarda en la base de datos.
     * 
     * @param x coordenada X en el sistema local del mapPane
     * @param y coordenada Y en el sistema local del mapPane
     * @param name nombre que darle a la anotacion
     * @param color color que darle a la anotacion
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    private void drawLine(double x, double y, String name, Color color) {
        GeoPoint start = projection.unproject(lineX, lineY);
        GeoPoint end = projection.unproject(x, y);
        lineInput = false;
        Annotation note = new Annotation(AnnotationType.LINE, name, colorToHex(color), 5.0, List.of(start, end));
        Annotation saved = LaSaforApp.app.addAnnotation(actividadActual, note);
        registrarAnotacion(saved);
        drawLine(saved);
    }

    /**
     * Metodo que toma una anotacion tipo linea y la dibuja sobre el mapa.
     * 
     * @param note anotacion a dibujar sobre el mapa.
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    private void drawLine(Annotation note) {
        GeoPoint start = note.getGeoPoints().get(0);
        GeoPoint end = note.getGeoPoints().get(1);

        Point2D p1 = projection.project(start);
        Point2D p2 = projection.project(end);

        Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        line.setStroke(hexToColor(note.getColor()));
        line.setStrokeWidth(5.0);
        line.setUserData("annotation");
        mapPane.getChildren().add(line);
    }

    /**
     * Abre un filechooser para importar un archivo gpx y la añade a la lista de actividades.
     * 
     * @author Hector Saez
     */
    @FXML
    private void importarGPX() {
        SportActivityApp app = LaSaforApp.app;
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar carrera GPX");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos GPX", "*.gpx"));
        File seleccionado = fc.showOpenDialog(mapPane.getScene().getWindow());

        if (seleccionado != null) {
            actividadActual = app.importActivity(seleccionado);
            int numPuntos = actividadActual.getTrackPoints().size();
            nombre = actividadActual.getName();
            
            TextInputDialog dialog = new TextInputDialog(nombre);
            dialog.setTitle("Renombrar actividad");
            dialog.setHeaderText("Cambiar nombre actividad");
            dialog.setContentText("Nuevo nombre:");
            dialog.initOwner(mapPane.getScene().getWindow());

            dialog.getDialogPane().expandedProperty().addListener((obs, old, val) -> {
                Platform.runLater(() -> {
                    dialog.getEditor().requestFocus();
                    dialog.getEditor().selectAll();
                });
            });

            dialog.setOnShown(e -> Platform.runLater(() -> {
                dialog.getEditor().requestFocus();
                dialog.getEditor().selectAll();
            }));

            dialog.showAndWait().ifPresent(newName -> {
                if(newName != null) nombre = newName;
            });
            
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Éxito");
            alerta.setHeaderText("Datos cargados correctamente");
            alerta.setContentText("Actividad: " + nombre + "\nTotal de puntos GPS: " + numPuntos);

            Window ventanaActual = mapPane.getScene().getWindow();
            alerta.initOwner(ventanaActual);
            alerta.showAndWait();
            
            app.renameActivity(actividadActual, nombre);
            MapRegion region = app.findMapForActivity(actividadActual);
            File mapFile = new File(region.getImagePath());
            buildMap(mapFile, region);
            mapPane.layout();
            double anchoReal = mapPane.getWidth();
            double altoReal = mapPane.getHeight();
            this.projection = new MapProjection(region, anchoReal, altoReal);
            mapPane.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Line);
            dibujarRutaPorVelocidad();
            verActividades();
            actividadActualId = null;
            abrirActividad(actividadActual);
            Platform.runLater(() -> centrarEnActividad(actividadActual));
        }
    }
    


    /**
     * Actualiza la lista de actividades cada vez que se añaden.
     * 
     * @author Jiaxiang Liu
     */
    private void verActividades() {
        
        User currentUser = LaSaforApp.app.getCurrentUser();
        
        activityList.setCellFactory(list -> new ListCell<Activity>() {
                @Override
                protected void updateItem(Activity a, boolean empty) {
                    super.updateItem(a, empty);

                    if (empty || a == null) {
                        setText(null);
                    } else {
                        setText(a.getName());
                    }
                }
            });
        activityList.getItems().setAll(currentUser.getActivities());
        
    }

    /**
     * Abre y mueve el mapa a la zona de inicio de la actividad seleccionada de la lista.
     * 
     * @author Jiaxiang Liu
     */
    @FXML
    private void activitySelected(MouseEvent event) {
        Activity actividad = activityList.getSelectionModel().getSelectedItem();
        if (actividad == null) return;
        actividadActual = actividad;
        abrirActividad(actividadActual);
        Platform.runLater(() -> centrarEnActividad(actividad));
    }

    /**
     * Metodo que abre una actividad.
     * 
     * Toma como parametro una actividad, encuentra el mapa adecuado
     * Cambia el mapa, dibuja sobre ella la carrera y las anotaciones son añadidas a la lista y el mapa
     * Abre una ventana que indica las estadisticas de la carrera
     * 
     * IA
     * 
     * @param actividad Actividad a abrirse
     * 
     * @authors Jiaxiang Liu, Hector Saez, Javier Blanch
     */
    private void abrirActividad(Activity actividad) {
        boolean esNuevaActividad = !Long.valueOf(actividad.getId()).equals(actividadActualId);
        actividadActualId = actividad.getId();

        SportActivityApp app = LaSaforApp.app;
        MapRegion region = app.findMapForActivity(actividad);

        if (esNuevaActividad) {
            File mapFile = new File(region.getImagePath());
            buildMap(mapFile, region);
            mapPane.layout();
            
            double anchoReal = mapPane.getWidth();
            double altoReal = mapPane.getHeight();
            this.projection = new MapProjection(region, anchoReal, altoReal);
            this.actividadActual = actividad;
            
            dibujarRutaPorVelocidad();
            try {
                javafx.fxml.FXMLLoader desLoader = new javafx.fxml.FXMLLoader(getClass().getResource("Desnivel.fxml"));
                javafx.scene.Parent desRoot = desLoader.load();
                DesnivelController desControl = desLoader.getController();
                desControl.setActivity(actividad);
                desControl.setMapContext(mapPane, projection);
                desControl.crearLeyendaVelocidad();

                java.util.List<TrackPoint> puntos = actividad.getTrackPoints();
                
                if (puntos != null && puntos.size() >= 2) {
                    TrackPoint pInicio = puntos.get(0);
                    TrackPoint pFin = puntos.get(puntos.size() - 1);

                    double distanciaMetros = 0;
                    
                    for (int i = 0; i < puntos.size() - 1; i++) {
                        distanciaMetros += puntos.get(i).distanceTo(puntos.get(i+1));
                    }
                    
                    double distanciaKm = distanciaMetros / 1000.0;
                    long segundosTotales = java.time.Duration.between(pInicio.getTime(), pFin.getTime()).getSeconds();
                    long horas = segundosTotales / 3600;
                    long minutos = (segundosTotales % 3600) / 60;
                    long segundos = segundosTotales % 60;
                    String tiempoFormateado = String.format("%02d:%02d:%02d", horas, minutos, segundos);

                    double velocidadMedia = 0.0;
                    if (segundosTotales > 0) {
                        velocidadMedia = (distanciaKm / (segundosTotales / 3600.0));
                    }

                    desControl.mostrarEstadisticasEnPantalla(tiempoFormateado, distanciaKm, velocidadMedia);
                }

               javafx.scene.layout.Region chartRegion = (javafx.scene.layout.Region) desRoot;
                   chartRegion.setMinHeight(200);
                   chartRegion.setMaxHeight(200);
                if (splitPaneMapa.getItems().size() == 1) {
                    splitPaneMapa.getItems().add(desRoot);
                } else if (splitPaneMapa.getItems().size() > 1) {
                    splitPaneMapa.getItems().set(1, desRoot);
                }
                javafx.scene.control.SplitPane.setResizableWithParent(desRoot, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            double mapWidth  = mapPane.getWidth() * zoomGroup.getScaleX();
            double mapHeight = mapPane.getHeight() * zoomGroup.getScaleY();
            TrackPoint p = actividad.getStartPoint();
            if (p != null) {
                Point2D pixelInicio = projection.project(p.getLatitude(), p.getLongitude());
                double viewW = map_scrollpane.getViewportBounds().getWidth();
                double viewH = map_scrollpane.getViewportBounds().getHeight();
                double scrollH = (pixelInicio.getX() - viewW / 2) / (mapWidth  - viewW);
                double scrollV = (pixelInicio.getY() - viewH / 2) / (mapHeight - viewH);
                final Timeline timeline = new Timeline();
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                    new KeyValue(map_scrollpane.hvalueProperty(), scrollH),
                    new KeyValue(map_scrollpane.vvalueProperty(), scrollV)));
                timeline.play();
            }

            if (anotacionesPorActividad.containsKey(actividad.getId())) {
                map_listview.getItems().setAll(
                    anotacionesPorActividad.get(actividad.getId()).stream()
                        .filter(a -> !anotacionesBorradas.contains(a.getId()))
                        .collect(java.util.stream.Collectors.toList())
                );
            } else {
                java.util.List<Annotation> fromBD = actividad.getAnnotations().stream()
                    .filter(a -> !anotacionesBorradas.contains(a.getId()))
                    .filter(a -> a.getId() > 0)
                    .collect(java.util.stream.Collectors.toList());
                anotacionesPorActividad.put(actividad.getId(), new java.util.ArrayList<>(fromBD));
                map_listview.getItems().setAll(fromBD);
            }
        }

        mapPane.getChildren().removeIf(n ->
            !(n instanceof ImageView) &&
            !"capaRutaGPX".equals(n.getId()) &&
            !"rastreador".equals(n.getId()) && 
            !"textoRastreador".equals(n.getId())
        );

        drawNotes();
    }
    /**
     * Cambia ventana a la de modificar perfil.
     * 
     * @author Jiaxiang Liu
     */
    @FXML
    private void modPerfil() throws IOException {
        LaSaforApp.modPerfil();
    }

    /**
     * Guarda la informacion y cambia ventana a la de iniciar sesion.
     * 
     * @author Jiaxiang Liu
     */
    @FXML
    private void logOut() {
        LaSaforApp.app.logout();
        LaSaforApp.abrirHub();
    }

    /**
     * Guarda la informacion y cierra la aplicacion.
     * 
     * @author Jiaxiang Liu
     */
    @FXML
    private void logOutExit() {
        SportActivityApp app = LaSaforApp.app;
        app.logout();
        Platform.exit();
    }
    
    /**
     * Metodo que cierra el mapa y restaura el tamaño de la ventana principal.
     * 
     * @author Jiaxiang Liu
     */
    private void cerrarStatMapa() {
        if (splitPaneMapa.getItems().size() > 1) splitPaneMapa.getItems().remove(1);
        LaSaforApp.abrirActividades();
    }
    
    /**
     * Quita la actividad seleccionada de la lista de actividades.
     * IA
     * Borrar una actividad vuelve al mapa por defecto
     * 
     * @author Jiaxiang Liu
     */
    @FXML
    private void removeActivity() {
        Activity activity = activityList.getSelectionModel().getSelectedItem();
        if (activity == null) return;
        
        MapRegion region = LaSaforApp.app.findMapForActivity(activity);
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar la actividad?");
        alert.setContentText("Esta acción eliminará permanentemente la actividad: " + activity.getName());
        alert.initOwner(activityList.getScene().getWindow()); 

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            LaSaforApp.app.removeActivity(activity);
            verActividades();

            File archivoMapa = new File("src/maps/upv.jpg");
            if (!archivoMapa.exists()) {
                archivoMapa = new File("maps/upv.jpg");
            }
            buildMap(archivoMapa, region);
            mapPane.layout();
            this.projection = new MapProjection(region, mapPane.getWidth(), mapPane.getHeight());
            map_listview.getItems().clear();
            cerrarStatMapa();
        }
    }
    
    /**
     * Este método abre un cuadro de diálogo para renombrar la actividad
     * seleccionada, aplica el cambio en el sistema y refresca la lista para 
     * mostrar el nuevo nombre.
     * IA
     * 
     * @author Jiaxiang Liu
     */
    @FXML
    void renameActivity() {
        Activity activity = activityList.getSelectionModel().getSelectedItem();
        if (activity == null) return;

        TextInputDialog dialog = new TextInputDialog(activity.getName());
        dialog.setTitle("Renombrar actividad");
        dialog.setHeaderText("Cambiar nombre actividad");
        dialog.setContentText("Nuevo nombre:");

        dialog.showAndWait().ifPresent(newName -> {
            LaSaforApp.app.renameActivity(activity, newName);
            verActividades(); 
            refreshActivity();
        });
    }
    
    /**
     * Este método solicita confirmación para borrar la anotación seleccionada,
     * actualiza las estructuras de datos, elimina el elemento de la interfaz y 
     * redibuja las notas restantes en el mapa.
     * IA
     * 
     * @author Hector Saez
     */
    @FXML
    void removeNote() {
        
        Annotation note = map_listview.getSelectionModel().getSelectedItem();
        
        if (note == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar la anotación?");
        alert.setContentText("Esta acción eliminará permanentemente la anotación seleccionada.");
        alert.initOwner(map_listview.getScene().getWindow());
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            anotacionesBorradas.add(note.getId());
            anotacionesPorActividad.getOrDefault(actividadActual.getId(), new java.util.ArrayList<>()).remove(note);
            LaSaforApp.app.removeAnnotation(note);
            map_listview.getItems().remove(note);
            mapPane.getChildren().removeIf(n ->
                !(n instanceof ImageView) &&
                !"capaRutaGPX".equals(n.getId()) &&
                !"rastreador".equals(n.getId()) && 
                !"textoRastreador".equals(n.getId())   
            );
            drawNotes();
        }
    }
        
    /**
     * Redibuja las anotaciones para representar cambios en tiempo real.
     * 
     * @author Jiaxiang Liu
     */
    private void refreshActivity() {
        if (actividadActual == null) return;
        mapPane.getChildren().removeIf(n -> "annotation".equals(n.getUserData()));
        drawNotes();
    }
    
    /**
     * Dibuja todas las anotaciones de la listview.
     * 
     * @author Jiaxiang Liu
     */
    private void drawNotes() {
        for (Annotation a : map_listview.getItems()) {
            switch (a.getType()) {
                case POINT -> drawPoint(a);
                case LINE -> drawLine(a);
                case TEXT -> drawPoi(a);
                case CIRCLE -> drawCircle(a);
            }
        }
    }
    
    /**
     * Cambia la foto de perfil y el texto del nombre de usuario.
     * Se llama cada vez que se abre la ventana para refrescar por cambios.
     * 
     * @author Jiaxiang Liu
     */
    private void setupUser() {
        user = LaSaforApp.app.getCurrentUser();
        
        if (user == null) return; 

        username.setText(user.getNickName());
        
        String path = user.getAvatarPath();
        if (path != null && !path.isBlank()) {
            javafx.scene.image.Image img = new javafx.scene.image.Image("file:" + path, false);
            userAvatar.setImage(img);
        }
        
        userAvatar.setFitWidth(100);
        userAvatar.setFitHeight(100);
        userAvatar.setPreserveRatio(true);
        javafx.scene.shape.Rectangle cut = new javafx.scene.shape.Rectangle(100, 100);
        userAvatar.setClip(cut);
    }
    
    /**
     * Abre el menu para añadir mapas.
     * IA
     * 
     * @author Javier Blanch
     */
    @FXML
    private void abrirMenuAnadirMapa() { 
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("AnadirMapa.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            
            stage.setTitle("Añadir Nuevo Mapa al Sistema");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initOwner(mapPane.getScene().getWindow());
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
        
        /**
         * Este método visualiza una ruta GPS sobre el mapa, aplicando un código 
         * de colores dinámico a cada segmento según la velocidad detectada y añadiendo 
         * marcadores para el inicio y fin del recorrido.
         * IA
         * 
         * @author Hector Saez
         */
        private void dibujarRutaPorVelocidad() {

           if (actividadActual == null || projection == null) return;

           java.util.List<TrackPoint> puntos = actividadActual.getTrackPoints();

           if (puntos == null || puntos.size() < 2) return;

           javafx.scene.layout.Pane contenedorRuta = new javafx.scene.layout.Pane();

           contenedorRuta.setId("capaRutaGPX");
           contenedorRuta.setMinWidth(mapPane.getWidth());
           contenedorRuta.setMinHeight(mapPane.getHeight());
           contenedorRuta.setPrefSize(mapPane.getWidth(), mapPane.getHeight());
           contenedorRuta.setMouseTransparent(true);

           mapPane.getChildren().add(contenedorRuta);

           double distanciaTotalMetros = 0;

           for (int i = 0; i < puntos.size() - 1; i++) {
               TrackPoint p1 = puntos.get(i);
               TrackPoint p2 = puntos.get(i + 1);

               double distMetros = p1.distanceTo(p2);
               distanciaTotalMetros += distMetros; 

               Point2D pixel1 = projection.project(p1.getLatitude(), p1.getLongitude());
               Point2D pixel2 = projection.project(p2.getLatitude(), p2.getLongitude());

               if (Double.isNaN(pixel1.getX()) || Double.isNaN(pixel1.getY()) ||
                   Double.isNaN(pixel2.getX()) || Double.isNaN(pixel2.getY())) {
                   continue; 
               }

               javafx.scene.shape.Line segmento = new javafx.scene.shape.Line(
                   pixel1.getX(), pixel1.getY(), 
                   pixel2.getX(), pixel2.getY()
               );

               double velocidadKmH = 0.0;
               try {
                   long segundosTramo = java.time.Duration.between(p1.getTime(), p2.getTime()).getSeconds();
                   if (segundosTramo > 0) {
                       double distKm = distMetros / 1000.0;
                       double horasTramo = segundosTramo / 3600.0;
                       velocidadKmH = distKm / horasTramo;
                   }
               } catch (Exception e) {
                   velocidadKmH = 25.0; 
               }

               if (velocidadKmH < 35.0) {
                    segmento.setStroke(Color.web("#2c3e50"));
                } else if (velocidadKmH <= 40.0) {
                    segmento.setStroke(Color.web("#7f8c8d"));
                } else {
                    segmento.setStroke(Color.web("#27ae60"));
                }

               segmento.setStrokeWidth(5.0);
               contenedorRuta.getChildren().add(segmento);
           }

           TrackPoint inicio = puntos.get(0);
           Point2D pixelInicio = projection.project(inicio.getLatitude(), inicio.getLongitude());
           javafx.scene.shape.Circle nodoInicio = new javafx.scene.shape.Circle(pixelInicio.getX(), pixelInicio.getY(), 12);
           nodoInicio.setFill(Color.LIME); 
           nodoInicio.setStroke(Color.WHITE);
           nodoInicio.setStrokeWidth(3.0);

           TrackPoint fin = puntos.get(puntos.size() - 1);
           Point2D pixelFin = projection.project(fin.getLatitude(), fin.getLongitude());
           javafx.scene.shape.Circle nodoFin = new javafx.scene.shape.Circle(pixelFin.getX(), pixelFin.getY(), 12);
           nodoFin.setFill(Color.RED); 
           nodoFin.setStroke(Color.WHITE);
           nodoFin.setStrokeWidth(3.0);

           contenedorRuta.getChildren().addAll(nodoInicio, nodoFin);
           contenedorRuta.toFront();

           mostrarEstadisticas(distanciaTotalMetros, puntos);
        }
    
    /**
     * Muestra las estadisticas de cada carrera.
     * 
     * @param distanciaMetros distancia de la carrera
     * @param puntos lista de TrackPoints de la carrera
     * 
     * @authors Javier Blanch
     */
    private void mostrarEstadisticas(double distanciaMetros, java.util.List<TrackPoint> puntos) {
       if (puntos == null || puntos.size() < 2) return;
       double distanciaKm = distanciaMetros / 1000.0;
       
       TrackPoint pInicio = puntos.get(0);
       TrackPoint pFin = puntos.get(puntos.size() - 1);
       
       long segundosTotales = java.time.Duration.between(pInicio.getTime(), pFin.getTime()).getSeconds();
       long horas = segundosTotales / 3600;
       long minutos = (segundosTotales % 3600) / 60;
       long segundos = segundosTotales % 60;
       String tiempoFormateado = String.format("%02d:%02d:%02d", horas, minutos, segundos);
       double velocidadMedia = 0.0;
       if (segundosTotales > 0) {
           velocidadMedia = (distanciaKm / (segundosTotales / 3600.0));
       }
       
   }

    /**
     * Abre la ventana de sesiones.
     * 
     * @author Jiaxiang Liu
     */
    @FXML
    private void verSesiones() {
        LaSaforApp.abrirHistorial();
    }
    
    
    /**
     * Abre la ventana de estadisticas acumuladas.
     * IA
     * 
     * @author Javier Blanch
     */
    @FXML
    private void abrirEstadisticas() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Estadisticas.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();

            stage.setTitle("Mis Estadísticas Acumuladas");
            try {
                stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/resources/logo.png")));
            } catch (Exception ex) {}
            
            stage.setScene(new javafx.scene.Scene(root));

            stage.initOwner(mapPane.getScene().getWindow());
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre dialogo para modificar la anotacion.
     * Permite cambiar el nombre, color.
     * 
     * @authors Jiaxiang Liu, Hector Saez
     */
    @FXML
    private void modifyNote() {
        Annotation note = map_listview.getSelectionModel().getSelectedItem();
        if (note == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar anotación");
        dialog.setHeaderText("Modificar nombre y color");

        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        TextField nameField = new TextField(note.getText());
        ColorPicker colorPicker = new ColorPicker(hexToColor(note.getColor()));

        VBox vbox = new VBox(10,
            new Label("Nombre:"), nameField,
            new Label("Color:"), colorPicker
        );
        dialog.getDialogPane().setContent(vbox);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            String nuevoNombre = nameField.getText().trim().isEmpty() ? note.getText() : nameField.getText().trim();
            String nuevoColor = colorToHex(colorPicker.getValue());

            anotacionesBorradas.add(note.getId());
            anotacionesPorActividad.getOrDefault(actividadActual.getId(), new java.util.ArrayList<>()).remove(note);
            LaSaforApp.app.removeAnnotation(note);
            map_listview.getItems().remove(note); // ← añade esto

            note.setText(nuevoNombre);
            note.setColor(nuevoColor);
            Annotation saved = LaSaforApp.app.addAnnotation(actividadActual, note);
            registrarAnotacion(saved);

            mapPane.getChildren().removeIf(n ->
                !(n instanceof ImageView) &&
                !"capaRutaGPX".equals(n.getId()) &&
                !"rastreador".equals(n.getId()) && 
                !"textoRastreador".equals(n.getId())
            );
            drawNotes();
        }
    }
    
    /**
     * Registra una id a la anotacion para añadir al mapa.
     * IA
     * 
     * @param saved anotacion que guardar
     * 
     * @author Hector Saez
     */
    private void registrarAnotacion(Annotation saved) {
        long id = actividadActual.getId();
        anotacionesPorActividad.computeIfAbsent(id, k -> new java.util.ArrayList<>()).add(saved);
        map_listview.getItems().add(saved);
    }
    
    /**
     * Metodo de invitado para autenticar sesion
     */
    @FXML
    private void auth(ActionEvent event) {
        LaSaforApp.abrirHub();
    }
    
    /**
     * Dirige la pantalla al centro de las coordenadas de la actividad en cualquier nivel de zoom.
     * IA
     * 
     * @param actividad en la que centrar la pantalla
     * 
     * @author Hector Saez
     */
    private void centrarEnActividad(Activity actividad) {
        
        if (actividad == null || projection == null) return;
        List<TrackPoint> puntos = actividad.getTrackPoints();
        if (puntos == null || puntos.isEmpty()) return;

        double sumX = 0, sumY = 0;
        
        for (TrackPoint p : puntos) {
            Point2D px = projection.project(p.getLatitude(), p.getLongitude());
            sumX += px.getX();
            sumY += px.getY();
        }
        
        double cx = sumX / puntos.size();
        double cy = sumY / puntos.size();
        
        centrarEnPunto(cx, cy);
    }
    
    /**
    * Ayuda a centrar las anotaciones en el medio e la pantalla en cualquier nivel de zoon.
    * IA
    * 
    * @param x coordenada x de la anotacion
    * @param y coordenada y de la anotacion
    * 
    * @author Hector Saez
    */
    private void centrarEnPunto(double x, double y) {
        double escala = zoomGroup.getScaleX();
        double contentW = mapPane.getWidth() * escala;
        double contentH = mapPane.getHeight() * escala;
        double viewW = map_scrollpane.getViewportBounds().getWidth();
        double viewH = map_scrollpane.getViewportBounds().getHeight();

        double scrollH = (x * escala - viewW / 2.0) / (contentW - viewW);
        double scrollV = (y * escala - viewH / 2.0) / (contentH - viewH);

        scrollH = Math.max(0, Math.min(1, scrollH));
        scrollV = Math.max(0, Math.min(1, scrollV));

        final double fH = scrollH;
        final double fV = scrollV;

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
            new KeyValue(map_scrollpane.hvalueProperty(), fH),
            new KeyValue(map_scrollpane.vvalueProperty(), fV)));
        timeline.play();
    }
}
    
