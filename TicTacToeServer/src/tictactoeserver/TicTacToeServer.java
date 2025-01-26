package tictactoeserver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import tictactoedb.DatabaseDao;
import tictactoedb.DatabaseDaoImpl;
import tictactoedb.NetworkAccessLayer;
import static tictactoeserver.ServerController.playersList;
import utilities.Codes;

public class TicTacToeServer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML file and link it to the OnboardStatisticController
        FXMLLoader loader = new FXMLLoader(getClass().getResource("OnboardStatistic.fxml")); // Ensure this path is correct
        Parent root = loader.load();

        // Obtain the controller instance
        OnboardStatisticController controller = loader.getController();

        // Initialize the Database DAO
        //  DatabaseDao dp = new DatabaseDaoImpl();
        // If necessary, pass the DAO or other dependencies to the controller
        // controller.setDatabaseDao(dp);
        // Set up the scene and stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Tic Tac Toe Server");
        stage.show();
        stage.setOnCloseRequest((e) -> {
            if (controller.isStarted) {
                controller.server.stopServer();
            }
            for (ServerController player : playersList) {
                ArrayList serverCloseRequest = new ArrayList();
                serverCloseRequest.add(Codes.SERVER_CLOSE_CODE);
                player.outputStream.println(serverCloseRequest);
                player.currentPlayer.setIsOnline(false);
                player.currentPlayer.setIsPlaying(false);
                try {
                    player.currentPlayer.setIsOnline(false);
                    player.currentPlayer.setIsPlaying(false);
                    NetworkAccessLayer.logout(player.currentPlayer);
                    NetworkAccessLayer.updateUserState(player.currentPlayer);
                   // playersList.remove(player);

                } catch (SQLException ex) {
                    Logger.getLogger(OnboardStatisticController.class.getName()).log(Level.SEVERE, null, ex);
                }
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
