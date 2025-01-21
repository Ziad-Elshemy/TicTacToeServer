/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author youse
 */
public class OnboardStatisticController implements Initializable {

    @FXML
    private TextArea received_data_area;
    @FXML
    private TextArea top_players_area;
    @FXML
    private Button start_button;
    @FXML
    private PieChart player_status_chart;
    @FXML
    private TextArea available_players_area;
    
    public Server server;
    public boolean isStarted =false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            appendTextToArea(received_data_area,"Welcome! Press START to begin.\n");
            start_button.setText("START");
            start_button.setStyle("-fx-background-color:   #ffa62b;");
    }    

    @FXML
    private synchronized void handleStartButtonAction(ActionEvent event) {
        try {
            if (!isStarted) {
                server = new Server(); 
                isStarted = true;
                start_button.setText("STOP");
                start_button.setStyle("-fx-background-color: GREEN;");
                appendTextToArea(received_data_area, "Server started successfully.\n");  
            }else{
                server.stopServer();  
                isStarted = false;
                start_button.setText("START");
                start_button.setStyle("-fx-background-color:   #ffa62b;");
                appendTextToArea(received_data_area,"Server stopped successfully.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appendTextToArea(received_data_area,"Error: " + e.getMessage() + "\n");
        } 
    }
        /**
     * Appends the given text to the specified TextArea.
     *
     * @param textArea The TextArea to append text to.
     * @param text The text to append.
     */
    private void appendTextToArea(TextArea textArea, String text) {
        textArea.appendText(text);
    }
    
    public void updatePlayerStatusChart(int online, int offline, int inGame) {
 
    player_status_chart.getData().clear();

    int total = online + offline + inGame;

    if (total == 0) {
        total = 1;  // Set total to 1 to avoid division by zero
    }

    // Calculate the percentage for each section
    double onlinePercentage = (online / (double) total) * 100;
    double offlinePercentage = (offline / (double) total) * 100;
    double inGamePercentage = (inGame / (double) total) * 100;

    // Create PieChart.Data with percentages as labels
    PieChart.Data onlineData = new PieChart.Data(
            String.format("Online Players %.1f%%", onlinePercentage), online);
    PieChart.Data offlineData = new PieChart.Data(
            String.format("Offline Players %.1f%%", offlinePercentage), offline);
    PieChart.Data inGameData = new PieChart.Data(
            String.format("In Game %.1f%%", inGamePercentage), inGame);

    player_status_chart.setData(FXCollections.observableArrayList(onlineData, offlineData, inGameData));
}

    
}
