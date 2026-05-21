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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controlador del inicio de sesion
 *
 * @author Erik Tzv
 */
public class Inicio_de_sesionController implements Initializable{

    @FXML private TextField NickName_ini;
    @FXML private Label Err_nick_ini;
    @FXML private TextField Pass_reg;
    @FXML private Label Err_pass_ini;
    @FXML private ImageView Img_pass;
    @FXML private TextField Pass_shown;
    
    private static boolean pressed = false;  
    private String Pass;
    private String Nick;
    private boolean shown = false;
   
    
    
    /**
     * Inicializa el controlador de inicio de sesion
     * @author Erik Tzv
     */    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Pass_reg.textProperty().bindBidirectional(Pass_shown.textProperty());
        
        NickName_ini.setOnKeyTyped(e -> {
            Nick = NickName_ini.getText();
            if(shown){
                Err_nick_ini.setVisible(false);
                Err_pass_ini.setVisible(false);
            }
        });
        Pass_reg.setOnKeyTyped(e -> {
            Pass = Pass_reg.getText();  
            if(shown){
                Err_nick_ini.setVisible(false);
                Err_pass_ini.setVisible(false);
            }
        });
        Pass_shown.setOnKeyTyped(e->{
            if(shown){
                Err_nick_ini.setVisible(false);
                Err_pass_ini.setVisible(false);
            }
        });
    }    
    
    /**
     * Inicio de sesion si el nick y la contraseña son correctos
     * Si no avisa al usuario que uno de los dos es incorrecto por seguridad
     * @author Erik Tzv
     * @param event 
     */
    @FXML
    private void Ini_ses(ActionEvent event) {
        if(LaSaforApp.app.login(Nick,Pass)){  
            Err_nick_ini.setVisible(false);
            Err_pass_ini.setVisible(false);
            shown = false;
            LaSaforApp.abrirActividades();
            
        }else{
            Err_nick_ini.setVisible(true);
            Err_pass_ini.setVisible(true);
            Err_nick_ini.setText("El Nick name o la contraseña son incorrectos");
            Err_pass_ini.setText("El Nick name o la contraseña son incorrectos");
            shown = true;
        }
    }
   
    /**
     * Saca al usuario de la app
     * @author Erik Tzv
     */
    @FXML
    private void Cancel_reg() {         
            Platform.exit();
            System.exit(0);        
    }
    
    /**
     * Cambia la escena a la de "Registro de usuario" mediante un hyperlink
     * @author Erik Tzv
     */
    @FXML
    private void Reg_ses() {
        LaSaforApp.abrirReg();
    }

     /**
     * Muestra la contraseña
     * @author Erik Tzv
     */
    @FXML
    private void Pass_show() {
        if(getPressed()){
            disableShown();
            enableReg();            
            Img_pass.setImage(new Image(getClass().getResourceAsStream("/resources/ojo_cerrado.png")));
            cyclePressed();
        }else{
            enableShown();
            disableReg();            
            Img_pass.setImage(new Image(getClass().getResourceAsStream("/resources/ojo_abierto.png")));
            cyclePressed();
        }
        
    }
    
    /**
     * @author Erik Tzv
     */
    private static void cyclePressed(){
        pressed = !pressed;
    }
    /**
     * @author Erik Tzv
     * @return 
     */
    private static boolean getPressed(){
        return pressed;
    }
    /**
     * @author Erik Tzv
     */
    private void disableShown(){
        Pass_shown.setDisable(true);
        Pass_shown.setVisible(false);
    }
    /**
     * @author Erik Tzv
     */
    private void disableReg(){
        Pass_reg.setDisable(true);
        Pass_reg.setVisible(false);    
    }
    /**
     * @author Erik Tzv
     */
    private void enableShown(){
        Pass_shown.setDisable(false);
        Pass_shown.setVisible(true);
    }   
    /**
     * @author Erik Tzv
     */
    private void enableReg(){        
        Pass_reg.setDisable(false);
        Pass_reg.setVisible(true);
    }

}
