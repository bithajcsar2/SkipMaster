/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.unideb.inf.skipmaster.Controller;
import java.sql.*;
import javafx.scene.control.Alert;
import javax.sql.*;
/**
 *
 * @author bitha
 */
public class MySqlConnector {
  public Connection conn = null;
  public Connection openConnection()
   {
       try
       {
           String url = "jdbc:mysql://teitgamma.synology.me:32563/SkipMaster"
                   + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Budapest";
           Class.forName ("com.mysql.jdbc.Driver");
           conn = DriverManager.getConnection (url,"skipmdev","1H4t3CL4n6u4g3");
           System.out.println ("Database connection established");
       }
       catch (Exception e)
       {
             System.out.println(e);
             Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Couldn't connect to remote database!");

            alert.showAndWait(); 

       }
       return conn;
   }
   public void closeConnection(){
        try{
        conn.close();
         System.out.println ("Database connection terminated");          
        }
                  
        catch (Exception e) 
        { 
            System.out.println(e);
        }      
   }
}
