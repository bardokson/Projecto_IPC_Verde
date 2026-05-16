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
        Parent root;
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("Registro_usuario.fxml"));
        root = loader.load();
        roots.put("registro", root);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        scene = new Scene(root);
        stage.setTitle("Running la Safor - Equipo verde");
        stage.setScene(scene);
        stage.show();
        loader = new FXMLLoader(getClass().getResource("Inicio_de_sesion.fxml"));
        root = loader.load();
        roots.put("inicio_sesion", root);
        loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        root = loader.load();
        roots.put("actividades", root);

    }
   

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
