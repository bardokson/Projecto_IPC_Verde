/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import upv.ipc.sportlib.SportActivityApp;

/**
 * FXML Controller class
 *
 * @author bardokson
 */
public class Inicio_de_sesionController implements Initializable{

    @FXML private TextField NickName_ini;
    @FXML private Label Err_nick_ini;
    @FXML private TextField Pass_reg;
    @FXML private Label Err_pass_ini;
    
    private String Nick;    
    private String Pass;
    SportActivityApp app = SportActivityApp.getInstance();
    
    /**
     * Initializes the controller class.
     */    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        NickName_ini.setOnKeyTyped(e -> Nick = NickName_ini.getText());
        Pass_reg.setOnKeyTyped(e -> Pass = Pass_reg.getText());
        
    }    

    /**
     * Acepta el login si los datos entrados son correctos y usa el metodo abrirActividades() para pasar a la pantalla principal.
     */
    @FXML
    private void Acept_reg() {
        if(app.login(Nick,Pass)){  
            LaSaforApp.abrirActividades();
        }else{
            Err_nick_ini.setText("El Nick name o la contraseña son incorrectos");
            Err_pass_ini.setText("El Nick name o la contraseña son incorrectos");
        }
        
    }
    /**
     * Cancela el registro saliendo de la aplicacion.
     */
    @FXML
    private void Cancel_reg() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Saliendo de la app");
        alert.setHeaderText("¿Quiere salir de la app?");
        alert.setContentText("Para acceder a la app debe registrarse o iniciar sesión");
        Optional<ButtonType> result= alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){                        
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    private void Reg_ses(ActionEvent event) {
        LaSaforApp.abrirReg();
    }

}
