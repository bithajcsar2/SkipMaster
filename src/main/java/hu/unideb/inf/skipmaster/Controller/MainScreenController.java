package hu.unideb.inf.skipmaster.Controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Adam
 */
public class MainScreenController implements Initializable {

    @FXML
    private Label filenamelabel;
    @FXML
    private Button syncData;
    @FXML
    private TextField lessonTextBox;
    @FXML
    private TextField courseTypeTextBox;
    @FXML
    private TextField skipsLeft;
    @FXML
    private Button registerlessonbutton;
    @FXML
    private Button filebrowsebutton;
    @FXML
    private Button importfromfilebutton;
    @FXML
    private GridPane table;
    
    File timetable; // ezt adja vissza a fájlmegnyitó alprogram
    
    private int numberOfCourses;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        numberOfCourses=1; //1 mert a táblázat 2. sorától vannak a tényleges adatok ;)
        loadTable(false);
    }    

    @FXML
    private void registerLesson(ActionEvent event) 
    {
        Label course = new Label(lessonTextBox.getText());
        Label course_type = new Label(courseTypeTextBox.getText());
        int remainingSkips = Integer.parseInt(skipsLeft.getText());
        if(remainingSkips < 0)
            remainingSkips = 0;
        Label numberOfSkips = new Label(Integer.toString(remainingSkips));
        
        Button skipBtn = new Button("Skip");
        final int id = numberOfCourses;
        skipBtn.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                Skipped(id);
                syncWithDB();
            }   
        });
               
        if(remainingSkips == 0){
            course.setTextFill(Color.RED);
            course_type.setTextFill(Color.RED);
            numberOfSkips.setTextFill(Color.RED);
            skipBtn.setTextFill(Color.RED);
            skipBtn.setDisable(true);
        }
        table.add(skipBtn, 3, numberOfCourses);
        table.add(course, 0, numberOfCourses);
        table.add(course_type, 1, numberOfCourses);
        table.add(numberOfSkips, 2, numberOfCourses);
        GridPane.setMargin(course, new Insets(0,0,0,5));
        GridPane.setMargin(course_type, new Insets(0,0,0,5));
        GridPane.setMargin(numberOfSkips, new Insets(0,0,0,5));
        numberOfCourses++;
        
        /* 
            Itt kellene implementálni az új óra felvételét az adatbázisba
        
        */
    }

    @FXML
    private void browseFile(ActionEvent event) //fájltallózó alprogram
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Stage stage = new Stage();
        stage.setTitle("Órarend tallózása");
        timetable =  fileChooser.showOpenDialog(stage);
        filenamelabel.setText("Betallózott fájl: " + timetable.getName());
        importfromfilebutton.setDisable(false);
         
    }

    @FXML
    private void importTimeTable(ActionEvent event) 
    {
        if (timetable == null)
        {
            System.out.println("Hiba történt a fájl megynitása során!");
        }
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/SkipMaster?characterEncoding=UTF-8&user=root&password=asd123")) {
            Statement stmt=connection.createStatement();
            loadDataFromFile(timetable.getPath(), stmt);
            stmt.close();
            loadTable(true);
        }catch(Exception e){
            System.out.println(e);
        }
        /*
            Ide kellene implementálni a fájlból importálást
            A fájlra a 'timetable' változóval tudsz hivatkozni
        */
    }
    
    private void loadTable(boolean sync){
        
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/SkipMaster?characterEncoding=UTF-8&user=root&password=asd123")) {
            Statement stmt=connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + LoginScreenController.userLoggedIn + ";");
            stmt.close();
            if(sync){
                numberOfCourses=1;
                table.getChildren().clear();
            }     
            while(rs.next()){       
                Label course = new Label(rs.getString("course"));
                Label course_type = new Label(rs.getString("course_type"));
                int remainingSkips = 3 - rs.getInt("numberOfSkips");
                if(remainingSkips < 0)
                    remainingSkips = 0;
                Label numberOfSkips = new Label(Integer.toString(remainingSkips));
                Button skipBtn = new Button("Skip");
                final int id = numberOfCourses;
                skipBtn.setOnMouseClicked(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Skipped(id);
                        syncWithDB();
                    }
                    
                });
                
                if(remainingSkips == 0){
                    course.setTextFill(Color.RED);
                    course_type.setTextFill(Color.RED);
                    numberOfSkips.setTextFill(Color.RED);
                    skipBtn.setTextFill(Color.RED);
                    skipBtn.setDisable(true);
                }
                table.add(skipBtn, 3, numberOfCourses);
                table.add(course, 0, numberOfCourses);
                table.add(course_type, 1, numberOfCourses);
                table.add(numberOfSkips, 2, numberOfCourses);
                GridPane.setMargin(course, new Insets(0,0,0,5));
                GridPane.setMargin(course_type, new Insets(0,0,0,5));
                GridPane.setMargin(numberOfSkips, new Insets(0,0,0,5));
                numberOfCourses++;
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    @FXML
    private void syncWithDB () 
    {
        /*
            Szinkronizálás gomb nyomására hívódik meg. Lekérdezi az adatokat, és megjeleníti az UI-on. 
            Egyelőre nincs mit szinkronizálni XD
        */
        loadTable(true);
        
    }
    
     private void loadDataFromFile(String path, Statement st){
        try{
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String targynev;
            String[] buff;
            char type = 'E';
            ResultSet rs = st.executeQuery("select * from " + LoginScreenController.userLoggedIn);
            if(rs.next()){
                st.executeQuery("drop table if exists " + LoginScreenController.userLoggedIn);
                st.executeQuery("create table if not exists " + LoginScreenController.userLoggedIn + " (id int auto_increment, course varchar(100),course_type varchar(20), numberOfSkips int default 0, primary key(id));");
            }
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
                            st.executeQuery("insert into " +  LoginScreenController.userLoggedIn + "(course, course_type) values('" + targynev + "', 'előadás');");
                            break;
                        case 'G':
                            targynev = String.copyValueOf(charbuff);
                            st.executeQuery("insert into " +  LoginScreenController.userLoggedIn + "(course, course_type) values('" + targynev + "', 'gyakorlat');");
                            break;
                        case 'L':
                            targynev = String.copyValueOf(charbuff);
                            st.executeQuery("insert into " +  LoginScreenController.userLoggedIn + "(course, course_type) values('" + targynev + "', 'labor');");
                            break;
                    }
            }
            br.close();
        }catch(Exception e){
            System.out.println(e);
        }        
    }
    
     
    private void Skipped(int id){
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/SkipMaster?characterEncoding=UTF-8&user=root&password=asd123")) {
            Statement stmt=connection.createStatement();
            stmt.executeQuery("update " + LoginScreenController.userLoggedIn + " set numberOfSkips = numberOfSkips+1 where id ="+  id + ";");
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
