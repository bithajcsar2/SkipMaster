
package hu.unideb.inf.skipmaster.Controller;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;


public class RegisterScreenController implements Initializable {

    @FXML
    private TextField registerneptun; //regisztrációs ablak neptunID mezője
    @FXML
    private TextField registerpwd;    //regisztrációs ablak jelszó mezője

    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
    }    

    @FXML
    private void registerButtonPressed(ActionEvent event)
    {
        //regisztrációs ablak Regisztráció gombjának lenyomása
    }
    
    
}

