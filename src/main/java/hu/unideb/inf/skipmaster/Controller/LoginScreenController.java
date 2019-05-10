
package hu.unideb.inf.skipmaster.Controller;

import java.io.BufferedReader;
import java.io.FileReader;
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
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginScreenController implements Initializable {
            
    @FXML
    private TextField loginneptun; //bejelentkezési ablak neptunID mezője
    @FXML
    private PasswordField loginpwd; //bejelentkezési ablak jelszó mezője
    @FXML
    private Button loginbutton;
    
    public static String userLoggedIn; //A bejelentkezett user felhasználóneve
 
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
        
    }    

    @FXML
    private void loginButtonPressed(ActionEvent event) throws IOException
    {
        //bejelentkezési ablak Belépés gombjának lenyomása
        /*
            Itt kéne megcsinálni az adabázisból a lekérdezést
            
        */
        boolean successful_login = false; //hiba esetén is megnyílik a mainscreen; később vissza kell állítani falsera
        
        if(loginneptun.getText().isEmpty() || loginpwd.getText().isEmpty()){
            System.out.println("A neptun-kód/jelszó mező nem lehet üres.");
        }else{
            try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/SkipMaster?characterEncoding=UTF-8&user=root&password=asd123")) {
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
                    userLoggedIn = loginneptun.getText();
                   successful_login = true;
                  }
                }else{
                    System.out.println("Hibás neptunID/Nincs regisztrálva"); //Ez mehet majd a GUI-ra
                }
                connection.close();
           }catch(Exception e){
               System.out.println(e);
           }
        }
        
        if (successful_login) //ha sikeresen belépett a felhasználó, megnyílik a main screen, a login pedig bezáródik
        {
            Parent root3 = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/MainScreen.fxml"));
            Stage stage2 = new Stage();
            stage2.setTitle("SkipMaster");
            Scene scene = new Scene(root3);
            scene.getStylesheets().add("/styles/Styles.css");
            stage2.setScene(scene);
            stage2.show();
            Stage stage = (Stage) loginbutton.getScene().getWindow();
            stage.close();
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