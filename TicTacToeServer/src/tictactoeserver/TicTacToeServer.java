/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tictactoedb.DatabaseDao;

/**
 *
 * @author Ziad-Elshemy
 */
public class TicTacToeServer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception
    {
        //Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Parent root = new FXMLServerBase();
        DatabaseDao dp = new DatabaseDao();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest((e)->{
            try
            {
                Server.serverSocket.close();
            }
            catch (IOException ex) 
            {
                Logger.getLogger(TicTacToeServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
