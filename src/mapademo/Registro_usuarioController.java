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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import upv.ipc.sportlib.User;

/**
 * Controlador encargado del registro de sesion del usuario
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
    @FXML private Label Err_pass;
    @FXML private ImageView Avatar_reg;
    @FXML private TextField Pass_shown;
    @FXML private ImageView Img_pass;
    @FXML private VBox V_box;
    
    public User user;
    
    private static boolean pressed = false;
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
    
    /**
     * Inicializa el controlador
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){
        // TODO
        Pass_reg.textProperty().bindBidirectional(Pass_shown.textProperty());
        //Se encarga de enseñar el popover sobre la contraseña
        popover = new PopOver(new Label("  La contraseña debe tener entre 8 y 20 caracteres,  \n " 
                + "  con al menos una mayúscula, una minúscula, un   \n"
                + "             dígito y un símbolo (!@#$%&*()-+=)"));
        Info_pass.setOnMouseEntered(e -> popover.show(Info_pass)); 
    }    
    
    /**
     * Comprueba si todos los campos son cerrectos y cambia la escena a la de actividades
     * @throws IOException 
     */
    @FXML
    private void Acept_reg(ActionEvent event) throws IOException {
        
        if(Nick_ok && Email_ok && Pass_ok && Birth_ok){
            Err_tot.setVisible(false);
            boolean ok = LaSaforApp.app.registerUser(Nick, Email, Pass, Birth, Avatar_Path);
            boolean logged = LaSaforApp.app.login(Nick, Pass);
            if (ok && logged) LaSaforApp.abrirActividades();
                
        }else{
            Err_tot.setVisible(true);
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
     * HyperLink para pasar a la escena de inicio de sesion
     * @throws IOException 
     */
    @FXML
    private void Ini_ses(ActionEvent event) throws IOException {
        LaSaforApp.abrirSignIn();
    }
    
    /**
     * TextField del NickName, comprueba si el Nick tiene el formato correcto cada vez que se escribe un caracter
     * @param event 
     */
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
    
    /**
     * TextField del email, comprueba si el email tiene el formato correcto cada vez que se escribe un caracter
     * @param event 
     */
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

    /**
     * TextField de la contraseña, comprueba si la pass tiene el formato correcto cada vez que se escribe un caracter
     * Ademas muestra el popover con la info sobre la contraseña
     * @param event 
     */
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
    
    /**
     * DatePicker del año de nacimiento, comprueba la edad al poner la fecha de nacimiento
     * @param event 
     */
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
    /**
     * Permite al usuario poner una foto como avatar
     * Si no pone ninguna se le pondra una por defecto
     * @throws IOExceptionn
     */
    
    @FXML
    private void Avatar_reg() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar mapa JPG");
        fc.setInitialDirectory(new File("."));
        // Filtramos para que solo deje elegir archivos .jpg
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg", "*.jpeg"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg", "*.jpeg",".png"));
        
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
        V_box.setDisable(true);
        Pass_reg.setDisable(true);
        Pass_reg.setVisible(false);
    }
    private void enableShown(){
        Pass_shown.setDisable(false);
        Pass_shown.setVisible(true);
    }
    private void enableReg(){
        V_box.setDisable(false);
        Pass_reg.setDisable(false);
        Pass_reg.setVisible(true);
    }
}
    