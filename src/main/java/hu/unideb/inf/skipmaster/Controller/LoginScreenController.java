
package hu.unideb.inf.skipmaster.Controller;

import java.io.File;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class LoginScreenController implements Initializable {
            
    @FXML
    private TextField loginneptun; //bejelentkezési ablak neptunID mezője
    @FXML
    private PasswordField loginpwd; //bejelentkezési ablak jelszó mezője
    @FXML
    private Button loginbutton;
    @FXML
    private ImageView connectionIndicatorPic;
    @FXML
    private Text databaseOKText;
    
    public static String userLoggedIn; //A bejelentkezett user felhasználóneve
            
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        File tick = new File("src/main/resources/images/connectOK.png");
        Image connectOK = new Image(tick.toURI().toString());
        File cross = new File("src/main/resources/images/connectFAIL.png");
        Image connectFAIL = new Image(cross.toURI().toString());
        
        MySqlConnector connector = new MySqlConnector();
        Connection connect = connector.openConnection();
        {
            if (connect != null)
            {
                connectionIndicatorPic.setImage(connectOK);
                databaseOKText.setVisible(true);
            }
            else
            {
                connectionIndicatorPic.setImage(connectFAIL);
                databaseOKText.setText(" Csatlakozási hiba!");
                databaseOKText.setVisible(true);
            }
        }
        connector.closeConnection();
    }    

    @FXML
    private void loginButtonPressed(ActionEvent event) throws IOException
    {
        loginneptun.setText(loginneptun.getText().toUpperCase());
     
        MySqlConnector connector= new MySqlConnector();
        //bejelentkezési ablak Belépés gombjának lenyomása
        /*
            Itt kéne megcsinálni az adabázisból a lekérdezést
            
        */
        boolean successful_login = false; //hiba esetén is megnyílik a mainscreen; később vissza kell állítani falsera
        
        if(loginneptun.getText().isEmpty() || loginpwd.getText().isEmpty()){
            System.out.println("A neptun-kód/jelszó mező nem lehet üres.");
             Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Információ");
            alert.setContentText("A NEPTUN-kód/jelszó mező nem lehet üres!");

            alert.showAndWait(); //Ez mehet majd a GUI-ra
        }else{
            /*try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/SkipMaster?characterEncoding=UTF-8&user=root&password=asd123")) {*/
            try (Connection connection = connector.openConnection()){
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
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Nem sikerült bejelentkezni");
                        alert.setContentText("Hibás jelszó!");
                        alert.showAndWait(); //Ez mehet majd a GUI-ra
                  }else{
                    System.out.println("Bejelentkezve");
                    userLoggedIn = loginneptun.getText();
                   successful_login = true;
                  }
                }else{
                    System.out.println("Hibás neptunID/Nincs regisztrálva");
                     Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Nem sikerült bejelentkezni");
                    alert.setContentText("Nincs regisztrálva ilyen neptunID!");

                    alert.showAndWait(); //Ez mehet majd a GUI-ra
                }
                /*connection.close();*/
                connector.closeConnection();
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

    Parent root2 = null; //hogy csak egy egyszer lehessen a regisztrációs ablakot megnyitni
    @FXML
    private void registerNowButtonPressed(ActionEvent event) throws IOException 
    {
        if (root2 == null)
        {
        root2 = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/RegisterScreen.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Regisztráció");
        Scene scene = new Scene(root2);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setScene(scene);
        stage.showAndWait();
        root2 = null;
        }
    }
}

