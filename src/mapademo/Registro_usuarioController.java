/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import upv.ipc.sportlib.User;

/**
 * FXML Controller class
 *
 * @author bardokson
 */
public class Registro_usuarioController implements Initializable {

    private boolean Nick_ok = false;
    private boolean Email_ok = false;
    private boolean Pass_ok = false;
    private boolean Birth_ok = false;
    private PopOver popover;
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
    private DatePicker Birth;
    @FXML
    private Label Err_birth;
     @FXML
    private Label Err_tot;
    @FXML
    private ImageView Info_pass;
    @FXML
    private Button Pass_show;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        popover = new PopOver(new Label("  La contraseña debe tener entre 8 y 20 caracteres,  \n "
                + "  con al menos una mayúscula, una minúscula, un   \n"
                + "             dígito y un símbolo (!@#$%&*()-+=)"));
        Info_pass.setOnMouseEntered(e -> popover.show(Info_pass));  
    }    

    @FXML
    private void Acept_reg(ActionEvent event) {
        if(Nick_ok && Email_ok && Pass_ok && Birth_ok){
            Err_tot.setVisible(false);
            //Cambiar a la escena de actividades
        }else{
            Err_tot.setVisible(true);
        }
    }

    @FXML
    private void Cancel_reg(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void Ini_ses(ActionEvent event) {
        
    }
    @FXML
    private void Entering_Nick(KeyEvent event) {
        if(!User.checkNickName(NickName_reg.getText())){
            Err_nick.setVisible(true);
            Nick_ok = false;
        } else {
            Err_nick.setVisible(false);
            Nick_ok = true;
        }
    }

    @FXML
    private void Entering_email(KeyEvent event) {
        if(!User.checkEmail(Email_reg.getText())){
            Err_email.setVisible(true);
            Email_ok = false;
        } else {
            Err_email.setVisible(false);
            Email_ok = true; 
        }
    }

    @FXML
    private void Entering_pass(KeyEvent event) {
        
        if(!User.checkPassword(Pass_reg.getText())){
            if(!popover.isShowing()) popover.show(Info_pass);
            Pass_ok = false;
        } else {
            popover.hide();
            Pass_ok = true;
        }
    }

    @FXML
    private void Entering_birth(ActionEvent event) { 
        LocalDate birthLD = Birth.getValue();
        if(!User.isOlderThan(birthLD, 12)){
            Err_birth.setVisible(true);
            Birth_ok = false;
        }else{
            Err_birth.setVisible(false);
            Birth_ok = true;
        }
    }
    @FXML
    private void Pass_show(ActionEvent event) {
        boolean pressed = false;
        if(pressed){
            Pass_show.setOnAction(e -> Pass_show.setText("Hide"));
            pressed = false;
        }else{
            Pass_show.setOnAction(e -> Pass_show.setText("Show"));           
            pressed = true;
        }
    }
    
}
