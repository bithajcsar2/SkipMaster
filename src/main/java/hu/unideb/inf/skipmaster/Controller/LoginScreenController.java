
package hu.unideb.inf.skipmaster.Controller;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ResourceBundle;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginScreenController implements Initializable {
    
    @FXML
    private TextField loginneptun; //bejelentkezési ablak neptunID mezője
    @FXML
    private PasswordField loginpwd; //bejelentkezési ablak jelszó mezője
    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
        
    }    

    @FXML
    private void loginButtonPressed(ActionEvent event)
    {
        //bejelentkezési ablak Belépés gombjának lenyomása
        /*
            Itt kéne megcsinálni az adabázisból a lekérdezést
            
        */
        if(loginneptun.getText().isEmpty() || loginpwd.getText().isEmpty()){
            System.out.println("A neptun-kód/jelszó mező nem lehet üres.");
        }else{
            try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/SkipMaster?user=root&password=asd123")) {
                Statement stmt=connection.createStatement();
                ResultSet rs=stmt.executeQuery("select passwd from user where neptunID = '" + loginneptun.getText() + "';");
                if(rs.next()){
                   MessageDigest md = MessageDigest.getInstance("MD5");
                   byte[] buffer = md.digest(loginpwd.getText().getBytes());
                   BigInteger no = new BigInteger(1, buffer); 
                   String hashedPwd = no.toString(16); 
                   while (hashedPwd.length() < 32) { 
                       hashedPwd = "0" + hashedPwd; 
                   }
                   if(!(rs.getString(1).equals(hashedPwd))){
                      System.out.println("Hibás jelszó"); //Ez mehet majd a GUI-ra
                  }else{
                      System.out.println("Bejelentkezve");
                  }
                }else{
                    System.out.println("Hibás neptunID"); //Ez mehet majd a GUI-ra
                }

           }catch(Exception e){
               System.out.println(e);
           }
        }
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
