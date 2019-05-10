
package hu.unideb.inf.skipmaster.Controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
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
                    rs = stmt.executeQuery("select * from " + loginneptun.getText() + ";");
                    if(rs.next()){
                        System.out.println(rs.getString("course") + '(' + rs.getString("course_type") + ')');
                        while(rs.next()){
                           System.out.println(rs.getString("course") + '(' + rs.getString("course_type") + ')');
                        }
                   }else{
                        System.out.println("Még nincsenek feltöltve a tárgyaid, tedd meg most! ;)");
                        loadDataFromFile("targyak.csv", stmt); //Ide kell majd a path a fájlhoz a tallózásból.
                   }
                  }
                }else{
                    System.out.println("Hibás neptunID/Nincs regisztrálva"); //Ez mehet majd a GUI-ra
                }
                connection.close();
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
    
    private void loadDataFromFile(String path, Statement st){
        try{
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String targynev;
            String[] buff;
            char type = 'E';
            while((line = br.readLine()) != null){
                    buff = line.split(";");
                    String[] tkod = buff[2].split("-");
                    if(tkod.length <= 1)
                        continue;
                     
                    for (String tkod1 : tkod) {
                        tkod1 = tkod1.trim();
                        if (tkod1.endsWith("E")) {
                            type = 'E';
                            break;
                        } else if (tkod1.endsWith("G")) {
                            type = 'G';
                            break;
                        } else if (tkod1.endsWith("L")) {
                            type = 'L';
                            break;
                        }
                    }
                    int a = buff[2].indexOf('(');
                    char[] charbuff = new char[a];
                    buff[2].getChars(0, a, charbuff, 0);
                    switch(type){
                        case 'E':
                            targynev = String.copyValueOf(charbuff);
                            st.executeQuery("insert into " + loginneptun.getText() + "(course, course_type) values('" + targynev + "', 'előadás');");
                            break;
                        case 'G':
                            targynev = String.copyValueOf(charbuff);
                            st.executeQuery("insert into " + loginneptun.getText() + "(course, course_type) values('" + targynev + "', 'gyakorlat');");
                            break;
                        case 'L':
                            targynev = String.copyValueOf(charbuff);
                            st.executeQuery("insert into " + loginneptun.getText() + "(course, course_type) values('" + targynev + "', 'labor');");
                            break;
                    }
                
            }
            br.close();
        }catch(Exception e){
            System.out.println(e);
        }        
    }
    /*
    Ez majd akkor kell, ha meglesz a felhasználói felület a tárgyakhoz
    selectedCourse: amelyik tárgyat skipelte
    selectedCourseType: előadás|gyakorlat|labor
    private void increaseSkip(){
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/SkipMaster?characterEncoding=UTF-8&user=root&password=asd123")) {
            Statement stmt=connection.createStatement();
            stmt.executeQuery("update " + loginneptun.getText() + " set numberOfSkips = numberOfSkips+1 where course = '" + selectedCourse + "' and course_type = '" + selectedCourseType + "';");
        }catch(Exception e){
            System.out.println(e);
        }
    }
    */
}
