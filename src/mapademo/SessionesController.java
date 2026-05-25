/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import upv.ipc.sportlib.Session;
import upv.ipc.sportlib.User;
import java.time.Duration;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author altaraga
 */
public class SessionesController implements Initializable {

    @FXML private TableView<Session> tablaSesiones;
    @FXML private TableColumn<Session, String> sesion;
    @FXML private TableColumn<Session, String> time;
    @FXML private TableColumn<Session, Integer> numImport;
    @FXML private TableColumn<Session, Integer> numVista;
    @FXML private TableColumn<Session, Integer> numNote;
    @FXML private TableView<Session> tablaTotal;
    @FXML private TableColumn<Session, String> totalTime;
    @FXML private TableColumn<Session, Integer> totalImport;
    @FXML private TableColumn<Session, Integer> totalVista;
    @FXML private TableColumn<Session, Integer> totalNote;

    private User user = LaSaforApp.app.getCurrentUser();
    private List<Session> sesList = LaSaforApp.app.getSessionsByUser(user);
    
    /**
     * Inicializa el controlador de sesiones.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //GPT
        sesion.setCellValueFactory(
                cellData -> new javafx.beans.property.ReadOnlyStringWrapper("Sesión " + cellData.getValue().getId()));

        time.setCellValueFactory(
                cellData ->new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        (cellData.getValue().getDuration().toHours()) + " h " + 
                                (cellData.getValue().getDuration().toMinutes() % 60) + " min"));
        
        numImport.setCellValueFactory(
                cellData -> new javafx.beans.property.ReadOnlyObjectWrapper<>(cellData.getValue().getImportedActivities()));
        
        numVista.setCellValueFactory(
                cellData -> new javafx.beans.property.ReadOnlyObjectWrapper<>(cellData.getValue().getViewedActivities()));
        
        numNote.setCellValueFactory(
                cellData -> new javafx.beans.property.ReadOnlyObjectWrapper<>(cellData.getValue().getAnnotationsCreated()));
        //GPT
        
        tablaSesiones.setItems(javafx.collections.FXCollections.observableArrayList(sesList));
        
        int totalImported = 0;
        int totalViewed = 0;
        int totalNotes = 0;
        Duration totalDuration = Duration.ZERO;
        
        for (Session s : sesList) {
            totalImported += s.getImportedActivities();
            totalViewed += s.getViewedActivities();
            totalNotes += s.getAnnotationsCreated();
            totalDuration = totalDuration.plus(s.getDuration());
        }
        
        final int finalImported = totalImported;
        final int finalViewed = totalViewed;
        final int finalAnnotations = totalNotes;
        final String finalDuration = totalDuration.toHours() + " h " + (totalDuration.toMinutes() % 60) + " min";
        
        //GPT
        totalTime.setCellValueFactory(
                cellData ->new javafx.beans.property.ReadOnlyObjectWrapper<>(finalDuration));
        
        totalImport.setCellValueFactory(
                cellData -> new javafx.beans.property.ReadOnlyObjectWrapper<>(finalImported));
        
        totalVista.setCellValueFactory(
                cellData -> new javafx.beans.property.ReadOnlyObjectWrapper<>(finalViewed));
        
        totalNote.setCellValueFactory(
                cellData -> new javafx.beans.property.ReadOnlyObjectWrapper<>(finalAnnotations));
        
        if (!sesList.isEmpty()) {
            javafx.collections.ObservableList<Session> filaTotal = javafx.collections.FXCollections.observableArrayList(sesList.get(0));
            tablaTotal.setItems(filaTotal);
        }
        //GPT
    }    
    
    /**
     * Retorna la escena a las actividades.
     */
    @FXML
    private void volver() {
        LaSaforApp.abrirActividades();
    }
    
}
