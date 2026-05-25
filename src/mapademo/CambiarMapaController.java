/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author bardokson
 */
public class CambiarMapaController implements Initializable {

    @FXML private ListView<String> mapas;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        mapas.setItems(FXMLDocumentController.mapaOb);
    }    

    @FXML
    private void volver(ActionEvent event) {
       javafx.scene.Node source = (javafx.scene.Node) event.getSource();
       Stage stage = (Stage) source.getScene().getWindow();
       stage.close();
    }

    @FXML
    private void conf(ActionEvent event) {
        String mapa = mapas.getSelectionModel().getSelectedItem();
        FXMLDocumentController.cambiarMapa(mapa);
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
}
