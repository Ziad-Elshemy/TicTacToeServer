/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tictactoedb.DatabaseDao;
import tictactoedb.DatabaseDaoImpl;
import static tictactoeserver.ServerController.playersList;
import utilities.Codes;

/**
 *
 * @author Ziad-Elshemy
 */
public class TicTacToeServer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new FXMLServerBase();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest((e)->{
             for(ServerController player : playersList)
             {
                ArrayList serverCloseRequest = new ArrayList();
                serverCloseRequest.add(Codes.SERVER_CLOSE_CODE);
                player.outputStream.println(serverCloseRequest);
            }
        });
    }

   
    public static void main(String[] args) {
        launch(args);
    }
    
}
