package hu.unideb.inf.skipmaster.Controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import hu.unideb.inf.skipmaster.Course;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Adam
 */
public class MainScreenController implements Initializable {
    MySqlConnector connector= new MySqlConnector();
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
    private int localVersion; //fájlból
    private int remoteVersion;
    
    private List<Course> courses = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        try (Connection connection = connector.openConnection()) {
            Statement stmt=connection.createStatement();
            ResultSet rs = stmt.executeQuery("select version from user where neptunID = '" + LoginScreenController.userLoggedIn + "';");
            if(rs.next()){
                remoteVersion = rs.getInt("version");
            }
            stmt.close();
        }catch(Exception e){
            System.out.println(e);
        }

        numberOfCourses=1; //1 mert a táblázat 2. sorától vannak a tényleges adatok ;)
        if(remoteVersion > 0){
            syncWithDB();
        }
        connector.closeConnection();
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
        Button addBtn = new Button("Add");
        final String id = course.getText();
        final String id2 = course_type.getText();
        skipBtn.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                Skipped(id,id2);
            }   
        });
        
        addBtn.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                Added(id,id2);
            }   
        });
        
        courses.add(new Course(course.getText(), course_type.getText(), remainingSkips));
        fillTable(table, course, course_type, numberOfSkips, skipBtn, addBtn);

        localVersion++;
        
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fájlkezelési hiba");
            alert.setHeaderText(null);
            alert.setContentText("Hiba történt a fájl megynitása során!");
            alert.showAndWait();
            System.out.println("Hiba történt a fájl megynitása során!");
        }
        loadDataFromFile(timetable.getPath());
        loadTable(false);
        /*
            Ide kellene implementálni a fájlból importálást
            A fájlra a 'timetable' változóval tudsz hivatkozni
        */
    }
    
    private void fillTable(GridPane table, Label course, Label course_type, Label remainingSkips, Button skipBtn, Button addBtn){
        if(Integer.parseInt(remainingSkips.getText()) == 0){
            course.setTextFill(Color.RED);
            course_type.setTextFill(Color.RED);
            remainingSkips.setTextFill(Color.RED);
            skipBtn.setTextFill(Color.RED);
            skipBtn.setDisable(true);
        }
        table.add(skipBtn, 3, numberOfCourses);
        table.add(course, 0, numberOfCourses);
        table.add(course_type, 1, numberOfCourses);
        table.add(remainingSkips, 2, numberOfCourses);
        
        table.add(addBtn, 4, numberOfCourses);
        
        GridPane.setMargin(course, new Insets(0,0,0,5));
        GridPane.setMargin(course_type, new Insets(0,0,0,5));
        GridPane.setMargin(remainingSkips, new Insets(0,0,0,5));
        GridPane.setMargin(addBtn, new Insets(0,5,0,0));

        numberOfCourses++;
    }
    
    private void loadTable(boolean sync){
        numberOfCourses = 1;
        Label course;
        Label course_type;
        Label remainingSkips;
        Button skipBtn;
        Button addBtn;
        int remSkips;
        ResultSet rs;
        
        
        if(sync){
            try (Connection connection = connector.openConnection()) {
                Statement stmt=connection.createStatement();
                rs = stmt.executeQuery("select version from user where neptunID = '" + LoginScreenController.userLoggedIn + "';");
                if(rs.next()){
                    remoteVersion=rs.getInt("version");
                }
                if(remoteVersion > localVersion){
                    table.getChildren().clear();
                    courses.clear();
                    rs = stmt.executeQuery("select * from " + LoginScreenController.userLoggedIn + ";");
                    while(rs.next()){       
                        course = new Label(rs.getString("course"));
                        course_type = new Label(rs.getString("course_type"));
                        remSkips = rs.getInt("remainingSkips");
                        remainingSkips = new Label(Integer.toString(remSkips));
                        courses.add(new Course(course.getText(), course_type.getText(), remSkips));
                        skipBtn = new Button("Skip");
                        addBtn = new Button("Add");
                        final String id = course.getText();
                        final String id2 = course_type.getText();
                        skipBtn.setOnMouseClicked(new EventHandler() {
                            @Override
                            public void handle(Event event) {
                                Skipped(id,id2);
                            }   
                        });

                        addBtn.setOnMouseClicked(new EventHandler() {
                            @Override
                            public void handle(Event event) {
                                Added(id,id2);
                            }   
                        });
                        fillTable(table, course, course_type, remainingSkips, skipBtn, addBtn);
                        localVersion = remoteVersion;
                    }
                }else if(localVersion > remoteVersion){
                    table.getChildren().clear();
                    rs = stmt.executeQuery("select * from " + LoginScreenController.userLoggedIn + ";");
                    if(rs.next()){
                        stmt.executeUpdate("delete from " + LoginScreenController.userLoggedIn + ";");
                    }
                    for(Course c : courses){
                        course = new Label(c.getCourse());
                        course_type = new Label(c.getCourse_type());
                        remSkips = c.getNumberOfSkips();
                        remainingSkips = new Label(Integer.toString(remSkips));
                        skipBtn = new Button("Skip");
                        addBtn = new Button("Add");
                        final String id = course.getText();
                        final String id2 = course_type.getText();
                        skipBtn.setOnMouseClicked(new EventHandler() {
                            @Override
                            public void handle(Event event) {
                                Skipped(id,id2);
                            }   
                        });

                        addBtn.setOnMouseClicked(new EventHandler() {
                            @Override
                            public void handle(Event event) {
                                Added(id,id2);
                            }   
                        });
                        fillTable(table, course, course_type, remainingSkips, skipBtn, addBtn);
                        stmt.executeUpdate("insert into " +  LoginScreenController.userLoggedIn + "(course, course_type, remainingSkips) values('" + c.getCourse() + "', '" + c.getCourse_type() + "'," + c.getNumberOfSkips() + ");");
                    }
                    stmt.executeUpdate("update user set version = '" + localVersion + "' where neptunID = '" + LoginScreenController.userLoggedIn + "';");
                }
                stmt.close();
                connector.closeConnection();
            }catch(Exception e){
                System.out.println(e);
            }
        }else{
            table.getChildren().clear();
            for(Course c : courses){
                course = new Label(c.getCourse());
                course_type = new Label(c.getCourse_type());
                remSkips = c.getNumberOfSkips();
                remainingSkips = new Label(Integer.toString(remSkips));
                skipBtn = new Button("Skip");
                addBtn = new Button("Add");
                final String id = course.getText();
                final String id2 = course_type.getText();
                skipBtn.setOnMouseClicked(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Skipped(id,id2);
                    }   
                });

                addBtn.setOnMouseClicked(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Added(id,id2);
                    }   
                });
                fillTable(table, course, course_type, remainingSkips, skipBtn, addBtn);
            }
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
    
     private void loadDataFromFile(String path){
        courses.clear();
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
                            courses.add(new Course(targynev, "előadás", 3));
                            break;
                        case 'G':
                            targynev = String.copyValueOf(charbuff);
                            courses.add(new Course(targynev, "gyakorlat", 3));
                            break;
                        case 'L':
                            targynev = String.copyValueOf(charbuff);
                            courses.add(new Course(targynev, "labor", 3));
                            break;
                    }
            }
            br.close();
            localVersion++;
        }catch(Exception e){
            System.out.println(e);
        }        
    }
    
    private void Skipped(String course, String course_type){
        for(Course c : courses){
            if(c.getCourse().equals(course) && c.getCourse_type().equals(course_type)){
                c.setNumberOfSkips(c.getNumberOfSkips()-1);
            }
        }
        localVersion++;
        loadTable(false);
    }
     private void Added(String course, String course_type){
        for(Course c : courses){
            if(c.getCourse().equals(course) && c.getCourse_type().equals(course_type)){
                c.setNumberOfSkips(c.getNumberOfSkips()+1);
            }
        }
        localVersion++;
        loadTable(false);
    }
}
