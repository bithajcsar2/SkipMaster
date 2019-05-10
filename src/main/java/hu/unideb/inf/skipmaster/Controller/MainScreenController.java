package hu.unideb.inf.skipmaster.Controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private ComboBox<String> dayComboBox;
    @FXML
    private TextField hourTextBox;
    @FXML
    private TextField minTextBox;
    @FXML
    private TextField skipsLeft;
    @FXML
    private Button registerlessonbutton;
    @FXML
    private Button filebrowsebutton;
    @FXML
    private Button importfromfilebutton;
    
    File timetable; // ezt adja vissza a fájlmegnyitó alprogram
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        dayComboBox.getItems().addAll("Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek");
    }    

    @FXML
    private void registerLesson(ActionEvent event) 
    {
        String lesson = lessonTextBox.getText(); //óra neve
        String date = dayComboBox.getValue() +", " + hourTextBox.getText() + ":" + minTextBox.getText(); //óra ideje
        int skipsleft = Integer.parseInt(skipsLeft.getText()); //hátralévő hiányzások száma
        
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
        
        /*
            Ide kellene implementálni a fájlból importálást
            A fájlra a 'timetable' változóval tudsz hivatkozni
        */
    }
    
    @FXML
    private void syncWithDB (ActionEvent event) 
    {
        /*
            Szinkronizálás gomb nyomására hívódik meg. Lekérdezi az adatokat, és megjeleníti az UI-on. 
            Egyelőre nincs mit szinkronizálni XD
        */
    }
}
