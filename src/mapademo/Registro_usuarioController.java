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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author bardokson
 */
public class Registro_usuarioController implements Initializable {

    @FXML
    private TextField NickName_reg;
    @FXML
    private Label Err_nick;
    @FXML
    private TextField Email_reg;
    @FXML
    private Label Err_email;
    @FXML
    private TextField Pass_reg;
    @FXML
    private Label Err_pass;
    @FXML
    private DatePicker Birth;
    @FXML
    private Label Err_birth;

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
    private void Ini_ses(ActionEvent event) {
    }

    @FXML
    private void Pass_specs(MouseEvent event) {
    }
    
}
