package mapademo;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import upv.ipc.sportlib.User;

public class ModificarUsuarioController implements Initializable {

    @FXML private ImageView Avatar_mod;
    @FXML private DatePicker Birth_mod;
    @FXML private TextField Email_mod;
    @FXML private Label Err_birth;
    @FXML private Label Err_email;
    @FXML private Label Err_nick;
    @FXML private Label Err_pass;
    @FXML private Label Err_tot;
    @FXML private ImageView Img_pass;
    @FXML private ImageView Info_pass;
    @FXML private PasswordField Pass_mod;
    @FXML private Button Pass_show;
    @FXML private TextField Pass_shown;
    @FXML private VBox Vbox_pass;
    @FXML private Label name;
    
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
    private String PrevEmail = LaSaforApp.app.getCurrentUser().getEmail();
    private String PrevPass = LaSaforApp.app.getCurrentUser().getPassword();
    private LocalDate PrevBirth = LaSaforApp.app.getCurrentUser().getBirthDate();
    private Image PrevAvatar = LaSaforApp.app.getCurrentUser().getAvatar();
    private String PrevAvatarPath = LaSaforApp.app.getCurrentUser().getAvatarPath();
    private File Avatar_file;
    private Image Avatar;
    private String Avatar_Path;

    /**Abre filechooser para elegir una imagen que poner como avatar
     */
    @FXML
    void Avatar_mod() throws Exception{
        
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar mapa JPG");
        fc.setInitialDirectory(new File("."));
        // Filtramos para que solo deje elegir archivos .jpg
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg", "*.jpeg"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg", "*.jpeg",".png"));
        
        File file = fc.showOpenDialog(Avatar_mod.getScene().getWindow());
        
        if (file != null) {
            Avatar_file = file;
            Image image = new Image(file.toURI().toString());
            Avatar_mod.setImage(image);
            Avatar_Path = Avatar_file.getCanonicalPath();
            Avatar_mod.setFitWidth(60);
            Avatar_mod.setFitHeight(60);
            Avatar_mod.setPreserveRatio(true);
            Rectangle cut = new Rectangle(60, 60);
            Avatar_mod.setClip(cut);
        } else {Avatar_Path = PrevAvatarPath;}
    }
    
    /**
     * DatePicker del año de nacimiento, comprueba la edad al poner la fecha de nacimiento
     * @param event 
     */
    @FXML
    void Entering_birth(ActionEvent event) {
        Birth = Birth_mod.getValue();
        
        if (Birth == null) Birth = PrevBirth;
        
        if(!User.isOlderThan(Birth, 12)){
            Err_birth.setVisible(true);
            Birth_ok = false;
        }else{
            Err_birth.setVisible(false);
            Birth_ok = true;
        }
    }

    /**
     * TextField del email, comprueba si el email tiene el formato correcto cada vez que se escribe un caracter
     * @param event 
     */
    @FXML
    void Entering_email(KeyEvent event) {
        Email = Email_mod.getText();
        
        if (Email == null) Email = PrevEmail;
        
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
    void Entering_pass(KeyEvent event) {
        Pass = Pass_mod.getText();
        
        if (Pass == null) Pass = PrevPass;
        
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
    
    private static void cyclePressed() {pressed = !pressed;}
    
    private static boolean getPressed() {return pressed;}
    
    private void disableShown(){
        Pass_shown.setDisable(true);
        Pass_shown.setVisible(false);
    }
    private void disableReg(){
        Pass_mod.setDisable(true);
        Pass_mod.setVisible(false);
    }
    private void enableShown(){
        Pass_shown.setDisable(false);
        Pass_shown.setVisible(true);
    }
    private void enableReg(){
        Pass_mod.setDisable(false);
        Pass_mod.setVisible(true);
    }

    /**
     * Cancela la modificacion del perfil, no guarda datos y vuelve a pantalla actividades.
     */
    @FXML
    public void cancelMod() {
        LaSaforApp.abrirActividades();
    }

    /**
     * Guarda los cambios hechos, chequeando email, contraseña y fecha nacimiento.
     * Cambia a pantalla actividades.
     */
    @FXML
    public void saveChange() {
        
        String newEmail = Email_mod.getText().trim().isEmpty()
            ? PrevEmail : Email_mod.getText().trim();

        String newPass = Pass_mod.getText().trim().isEmpty()
            ? PrevPass : Pass_mod.getText().trim();

        LocalDate newBirth = Birth_mod.getValue() == null
            ? PrevBirth : Birth_mod.getValue();

        String newAvatarPath = Avatar_Path == null
            ? PrevAvatarPath : Avatar_Path;
        
        Email_ok = User.checkEmail(newEmail);
        Pass_ok = User.checkPassword(newPass);
        Birth_ok = User.isOlderThan(newBirth, 12);
        
        if(Email_ok && Pass_ok && Birth_ok){
            Err_tot.setVisible(false);
            boolean ok = LaSaforApp.app.updateCurrentUser(newEmail, newPass, newBirth, newAvatarPath);
            if (ok) LaSaforApp.abrirActividades();
        }else{
            Err_tot.setVisible(true);
        }
    }
    
    /**
     * Inicializa el controlador de
     * @param url
     * @param rb 
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        
        Birth_mod.setValue(PrevBirth);
        Email_mod.setText(PrevEmail);
        Avatar_mod.setImage(PrevAvatar);

        Pass_mod.textProperty().bindBidirectional(Pass_shown.textProperty());
        //Se encarga de enseñar el popover sobre la contraseña
        popover = new PopOver(new Label("  La contraseña debe tener entre 8 y 20 caracteres,  \n " 
                + "  con al menos una mayúscula, una minúscula, un   \n"
                + "             dígito y un símbolo (!@#$%&*()-+=)"));
        Info_pass.setOnMouseEntered(e -> popover.show(Info_pass));  
        name.setText(LaSaforApp.app.getCurrentUser().getNickName());
    }
}
