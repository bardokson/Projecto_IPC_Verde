/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapademo;

import java.util.HashMap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;

/**
 *
 * @author jose
 */
public class LaSaforApp extends Application {
    
    protected static SportActivityApp app = SportActivityApp.getInstance();

    private static Scene scene;
    
    private static HashMap<String,Parent> roots = new HashMap<>();
    
    /**
     * Cambia la raiz de la escena actual y ajusta el tamaño de ventana.
     * @param root nodo raíz que se mostrará en la escena
     * @param w ancho de la ventana en píxeles
     * @param h alto de la ventana en píxeles
     */
    static void setRoot(Parent root, int w, int h){
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.setWidth(w);
        stage.setHeight(h);
        stage.centerOnScreen();
    }
    
    /**
     * Cambia la raiz de la escena actual.
     * @param root nodo raíz que se mostrará en la escena
     */
    static void setRoot(Parent root){
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.centerOnScreen();
    }
    
    /**
     * Cambia la raiz de la escena actual llamando el metodo del mismo nombre que acepte como @param root.
     * @param clave identificador de la vista a cargar
     */
    static void setRoot(String clave){
        Parent root = roots.get(clave);
        if(root != null){
            setRoot(root);
        }else{
            System.err.printf("No se encuntra la escena: %s", clave);
        }
    }

    
    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Registro_usuario.fxml"));
        Parent root = loader.load();
        roots.put("registro", root);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        
        scene = new Scene(root);
        stage.setTitle("Registro");
        stage.setScene(scene);
        
        loader = new FXMLLoader(getClass().getResource("Inicio_de_sesion.fxml"));
        root = loader.load();
        roots.put("inicio_sesion", root);
        
        stage.show();
    }
   
    /**
     * Metodo para cambiar a la pantalla de actividades y inicia su tamaño de ventana por defecto.
     */
    public static void abrirActividades() {
        abrirActividades(1000, 700);
    }
    /**
     * Metodo para cambiar a la pantalla de actividades y inicia su tamaño de ventana.
     * @param w anchura de la ventana.
     * @param h altura de la ventana,
     */
    public static void abrirActividades(int w, int h) {
        try {
            FXMLLoader loader = new FXMLLoader(LaSaforApp.class.getResource("FXMLDocument.fxml"));
            Parent actividadesRoot = loader.load();
            LaSaforApp.roots.put("actividades", actividadesRoot);
            setRoot(actividadesRoot, w, h);
        } catch (Exception e) {e.printStackTrace();}
    }
    
    /**
     * Metodo para cambiar a la pantalla de modificar perfil y inicia su tamaño de ventana.
     */
    public static void modPerfil() {
        try {
            FXMLLoader loader = new FXMLLoader(LaSaforApp.class.getResource("ModificarUsuario.fxml"));
            Parent modRoot = loader.load();
            LaSaforApp.roots.put("modificar", modRoot);
            setRoot(modRoot, 480, 470);
        } catch (Exception e) {e.printStackTrace();}
    }
    
    /**
     * Metodo para cambiar a la pantalla de registro y inicia su tamaño de ventana.
     */
    public static void abrirReg() {
        try {
            FXMLLoader loader = new FXMLLoader(LaSaforApp.class.getResource("Registro_usuario.fxml"));
            Parent regRoot = loader.load();
            LaSaforApp.roots.put("registro", regRoot);
            setRoot(regRoot, 480, 470);
        } catch (Exception e) {e.printStackTrace();}
    }
    
    /**
     * Metodo para cambiar a la pantalla de iniciar sesion y inicia su tamaño de ventana.
     */
    public static void abrirSignIn() {
        try {
            FXMLLoader loader = new FXMLLoader(LaSaforApp.class.getResource("Inicio_de_sesion.fxml"));
            Parent signRoot = loader.load();
            LaSaforApp.roots.put("signin", signRoot);
            setRoot(signRoot, 400, 350);
        } catch (Exception e) {e.printStackTrace();}
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
