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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import upv.ipc.sportlib.User;

/**
 * FXML Controller class
 *
 * @author bardokson
 */
public class Inicio_de_sesionController implements Initializable{

    @FXML
    private TextField NickName_ini;
    @FXML
    private Label Err_nick_ini;
    @FXML
    private TextField Pass_reg;
    @FXML
    private Label Err_pass_ini;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void Acept_reg(ActionEvent event) {
      
        
    }

    @FXML
    private void Cancel_reg(ActionEvent event) {
    }

    @FXML
    private void Reg_ses(ActionEvent event) {
        LaSaforApp.setRoot("registro");
    }
    
}
