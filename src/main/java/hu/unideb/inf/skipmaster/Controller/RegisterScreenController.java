
package hu.unideb.inf.skipmaster.Controller;



import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterScreenController implements Initializable {
    MySqlConnector connector;
    @FXML
    private TextField registerneptun; //regisztrációs ablak neptunID mezője
    @FXML
    private PasswordField registerpwd;    //regisztrációs ablak jelszó mezője
    @FXML
    private Button registerButton;
    
    private String hashedPwd;

    public RegisterScreenController() {
        this.connector = new MySqlConnector();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        
    }    

    @FXML
    private void registerButtonPressed(ActionEvent event)
    {
        registerneptun.setText(registerneptun.getText().toUpperCase());
        //regisztrációs ablak Regisztráció gombjának lenyomása
        if(registerneptun.getText().isEmpty() || registerpwd.getText().isEmpty()){
            System.out.println("A neptun-kód/jelszó mező nem lehet üres.");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Információ");
            alert.setHeaderText(null);
            alert.setContentText("A NEPTUN-kód/jelszó mező nem lehet üres!");
            alert.showAndWait();
        }else{
            if (registerneptun.getText().length() != 6)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Információ");
                alert.setHeaderText(null);
                alert.setContentText("A NEPTUN-kódnak pontosan 6 karakterből kell állnia!");
                alert.showAndWait();
            } 
            else try{
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] buffer = md.digest(registerpwd.getText().getBytes());
                BigInteger no = new BigInteger(1, buffer); 
                hashedPwd = no.toString(16); 
                while (hashedPwd.length() < 32) { 
                    hashedPwd = "0" + hashedPwd; 
                }
                 /*try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/SkipMaster?characterEncoding=UTF-8&user=root&password=asd123")) {*/
                 try (Connection connection = connector.openConnection()){
                    Statement stmt=connection.createStatement();
                    ResultSet rs=stmt.executeQuery("select neptunID from user where neptunID = '" + registerneptun.getText() + "';");
                    if(rs.next()){
                       System.out.println("Ez a NEPTUN-kód már regisztrálva van!");
                       Alert alert = new Alert(Alert.AlertType.INFORMATION);
                       alert.setTitle("Adatbázis információ");
                       alert.setHeaderText(null);
                       alert.setContentText("Ez a NEPTUN-kód már regisztrálva van!");
                       alert.showAndWait();
                    }else{
                        stmt.executeUpdate("insert into user(neptunID, passwd) values('" + registerneptun.getText() + "', '" + hashedPwd + "');");
                        stmt.executeUpdate("create table if not exists " + registerneptun.getText() + " (id int auto_increment, course varchar(100),course_type varchar(20), remainingSkips int default 3, primary key(id));");
                        Stage stage = (Stage) registerButton.getScene().getWindow();
                        stage.close();
                        System.out.println("Sikeres regisztráció, most már bejelentkezhetsz!");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Sikeres regisztráció");
                        alert.setHeaderText(null);
                        alert.setContentText("Sikeres regisztráció, most már bejelentkezhetsz!");
                        alert.showAndWait();
                    }
                    /*connection.close();*/
                    connector.closeConnection();
                 }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }     
        }
    }   
}

