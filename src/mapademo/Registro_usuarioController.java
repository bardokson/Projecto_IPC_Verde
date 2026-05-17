/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.File;
import java.io.IOException;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import upv.ipc.sportlib.User;
import upv.ipc.sportlib.SportActivityApp;

/**
 * FXML Controller class
 *
 * @author bardokson
 */
public class Registro_usuarioController implements Initializable {
       
    @FXML private TextField NickName_reg;
    @FXML private Label Err_nick;
    @FXML private TextField Email_reg;
    @FXML private Label Err_email;
    @FXML private TextField Pass_reg;
    @FXML private DatePicker Birth_reg;
    @FXML private Label Err_birth;
    @FXML private Label Err_tot;
    @FXML private ImageView Info_pass;
    @FXML private Button Pass_show;
    @FXML private Label Err_pass;
    @FXML private ImageView Avatar_reg;
    
    public User user;
    
    private boolean Nick_ok = false;
    private boolean Email_ok = false;
    private boolean Pass_ok = false;
    private boolean Birth_ok = false;
    private PopOver popover;
    private String Nick;
    private String Email;
    private String Pass;
    private LocalDate Birth;
    private File Avatar_file;
    private Image Avatar;
    private String Avatar_Path;
    //private SportActivityApp app = SportActivityApp.getInstance();
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){
        // TODO
        popover = new PopOver(new Label("  La contraseña debe tener entre 8 y 20 caracteres,  \n "
                + "  con al menos una mayúscula, una minúscula, un   \n"
                + "             dígito y un símbolo (!@#$%&*()-+=)"));
        Info_pass.setOnMouseEntered(e -> popover.show(Info_pass));  
    }    

    @FXML
    private void Acept_reg(ActionEvent event) throws IOException {
        //LaSaforApp.setRoot("actividades"); //solo para testing, quitar luego
        if(Nick_ok && Email_ok && Pass_ok && Birth_ok){
            Err_tot.setVisible(false);
            boolean ok = LaSaforApp.app.registerUser(Nick, Email, Pass, Birth, Avatar_Path);
            boolean logged = LaSaforApp.app.login(Nick, Pass);
            
            //Cambiar a la escena de actividades
            if (ok && logged) LaSaforApp.setRoot("actividades");
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
    private void Ini_ses(ActionEvent event) throws IOException {
        LaSaforApp.setRoot("inicio_sesion");
    }
    @FXML
    private void Entering_Nick(KeyEvent event) {
        Nick = NickName_reg.getText();
        if(!User.checkNickName(Nick)){
            Err_nick.setVisible(true);
            Nick_ok = false;
        } else {
            Err_nick.setVisible(false);
            Nick_ok = true;
        }
    }

    @FXML
    private void Entering_email(KeyEvent event) {
        Email = Email_reg.getText();
        if(!User.checkEmail(Email)){
            Err_email.setVisible(true);
            Email_ok = false;
        } else {
            Err_email.setVisible(false);
            Email_ok = true; 
        }
    }

    @FXML
    private void Entering_pass(KeyEvent event) {
        Pass = Pass_reg.getText();
        if(!User.checkPassword(Pass)){
            if(!popover.isShowing()) popover.show(Info_pass);
            Err_pass.setVisible(true);
            Pass_ok = false;
        } else {
            popover.hide();
            Err_pass.setVisible(false);
            Pass_ok = true;
        }
    }

    @FXML
    private void Entering_birth(ActionEvent event) { 
        Birth = Birth_reg.getValue();
        if(!User.isOlderThan(Birth, 12)){
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

    @FXML
    private void Avatar_reg(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar mapa JPG");
        fc.setInitialDirectory(new File("."));
        // Filtramos para que solo deje elegir archivos .jpg
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg", "*.jpeg"));
        
        File file = fc.showOpenDialog(Avatar_reg.getScene().getWindow());
        
        if (file != null) {
            Avatar_file = file;
            // Escribimos la ruta en el cajón de texto para que el usuario la vea
            /*Avatar_reg.setImage(new Image(getClass().getResourceAsStream(Avatar_file.getAbsolutePath())));
            Avatar = Avatar_reg.getImage();*/
            
            Image image = new Image(file.toURI().toString());

            Avatar_reg.setImage(image);
            //Avatar = image;
            Avatar_Path = Avatar_file.getCanonicalPath();
            Avatar_reg.setFitWidth(60);
            Avatar_reg.setFitHeight(60);
            Avatar_reg.setPreserveRatio(true);
            Rectangle cut = new Rectangle(60, 60);
            Avatar_reg.setClip(cut);
        }
    }
    
}
    
