
package hu.unideb.inf.skipmaster.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginScreenController implements Initializable {
    
    @FXML
    private TextField loginneptun; //bejelentkezési ablak neptunID mezője
    @FXML
    private TextField loginpwd; //bejelentkezési ablak jelszó mezője
    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
    }    

    @FXML
    private void loginButtonPressed(ActionEvent event) 
    {
        //bejelentkezési ablak Belépés gombjának lenyomása
    }

    @FXML
    private void registerNowButtonPressed(ActionEvent event) throws IOException 
    {
        Parent root2 = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/RegisterScreen.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Regisztráció");
        Scene scene = new Scene(root2);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setScene(scene);
        stage.show();
    }
    
}
