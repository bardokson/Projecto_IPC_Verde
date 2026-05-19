/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
    @FXML private ImageView Img_pass;
    @FXML private TextField Pass_shown;
    
    private static boolean pressed = false;  
    private String Pass;
    private String Nick;
   
    
    
    /**
     * Initializes the controller class.
     */    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Pass_reg.textProperty().bindBidirectional(Pass_shown.textProperty());
        NickName_ini.setOnKeyTyped(e -> Nick = NickName_ini.getText());
        Pass_reg.setOnKeyTyped(e -> Pass = Pass_reg.getText());
    }    
    
    /**
     * Inicio de sesion si el nick y el pass son correctos
     * @param event 
     */
    @FXML
    private void Ini_ses(ActionEvent event) {
        if(LaSaforApp.app.login(Nick,Pass)){  
            Err_nick_ini.setVisible(false);
            Err_pass_ini.setVisible(false);
            
        }else{
            Err_nick_ini.setVisible(true);
            Err_pass_ini.setVisible(true);
            Err_nick_ini.setText("El Nick name o la contraseña son incorrectos");
            Err_pass_ini.setText("El Nick name o la contraseña son incorrectos");
        }
    }
   
    /**
     * Saca al usuario de la app
     * @param event 
     */
    @FXML
    private void Cancel_reg(ActionEvent event) {         
            Platform.exit();
            System.exit(0);        
    }
    
    /**
     * Cambia la escena a la de "Registro de usuario" mediante un hyperlink
     * @param event 
     */
    @FXML
    private void Reg_ses(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("Registro_usuario.fxml")
        );

        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource())
            .getScene()
            .getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle("Registro");
        stage.show();

        } catch (IOException e) {}
    }

     /**
     * Muestra la contraseña
     * @param event 
     */
    @FXML
    private void Pass_show(ActionEvent event) {
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
    
    private static void cyclePressed(){
        pressed = !pressed;
    }
    
    private static boolean getPressed(){
        
        return pressed;
    }
    
    private void disableShown(){
        Pass_shown.setDisable(true);
        Pass_shown.setVisible(false);
    }
    private void disableReg(){
        Pass_reg.setDisable(true);
        Pass_reg.setVisible(false);    
    }
    private void enableShown(){
        Pass_shown.setDisable(false);
        Pass_shown.setVisible(true);
    }
    private void enableReg(){        
        Pass_reg.setDisable(false);
        Pass_reg.setVisible(true);
    }

    


}
