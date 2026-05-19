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
    
    static void setRoot(Parent root){
        scene.setRoot(root);
    }
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
   
    public static void abrirActividades() {
    try {
        FXMLLoader loader = new FXMLLoader(LaSaforApp.class.getResource("FXMLDocument.fxml"));
        Parent actividadesRoot = loader.load();
        LaSaforApp.roots.put("actividades", actividadesRoot);
        LaSaforApp.scene.setRoot(actividadesRoot);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
