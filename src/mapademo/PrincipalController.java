/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author bardokson
 */
public class PrincipalController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void Reg(ActionEvent event) {
        LaSaforApp.abrirReg();
    }

    @FXML
    private void Ini_ses(ActionEvent event) {
        LaSaforApp.abrirSignIn();
    }

    @FXML
    private void Salir(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
    
}
