package tictactoeserver;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import tictactoedb.DatabaseDaoImpl;

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
    public boolean isStarted = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appendTextToArea(received_data_area, "Welcome! Press START to begin.\n");
        start_button.setText("START");
        start_button.setStyle("-fx-background-color:   #ffa62b;");
        updateAvailablePlayers(); // Update available players on startup
        updateTopPlayers(); // Update top players on startup
        updatePlayerStatusChart();
    }

    @FXML
    private synchronized void handleStartButtonAction(ActionEvent event) {
        try {
            if (!isStarted) {
                server = new Server(this);
                isStarted = true;
                start_button.setText("STOP");
                start_button.setStyle("-fx-background-color: GREEN;");
                appendTextToArea(received_data_area, "Server started successfully.\n");

                // Periodically update players' data when the server starts
                updateAvailablePlayers();
                updateTopPlayers();
            } else {
                server.stopServer();
                isStarted = false;
                start_button.setText("START");
                start_button.setStyle("-fx-background-color:   #ffa62b;");
                appendTextToArea(received_data_area, "Server stopped successfully.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appendTextToArea(received_data_area, "Error: " + e.getMessage() + "\n");
        }
    }

    /**
     * Appends the given text to the specified TextArea.
     *
     * @param textArea The TextArea to append text to.
     * @param text The text to append.
     */
    public void appendTextToArea(TextArea textArea, String text) {
        textArea.appendText(text);
    }

    /**
     * Updates the player status chart with online, offline, and in-game counts.
     */
   public void updatePlayerStatusChart() {
    try {
        int[] counts = DatabaseDaoImpl.getPlayerStatusCounts();
        int online = counts[0];
        int offline = counts[1];
        int inGame = counts[2];

        Platform.runLater(() -> {
            player_status_chart.getData().clear();

            int total = online + offline + inGame;
            if (total == 0) {
                total = 1; // Avoid division by zero
            }

            double onlinePercentage = (online / (double) total) * 100;
            double offlinePercentage = (offline / (double) total) * 100;
            double inGamePercentage = (inGame / (double) total) * 100;

            PieChart.Data onlineData = new PieChart.Data(
                    String.format("Online%.1f%%", onlinePercentage), online);
            PieChart.Data offlineData = new PieChart.Data(
                    String.format("Offline%.1f%%", offlinePercentage), offline);
            PieChart.Data inGameData = new PieChart.Data(
                    String.format("InGame%.1f%%", inGamePercentage), inGame);

            player_status_chart.setData(FXCollections.observableArrayList(onlineData, offlineData, inGameData));
        });
    } catch (Exception e) {
        Platform.runLater(() -> appendTextToArea(received_data_area, "Error fetching player status: " + e.getMessage() + "\n"));
    }
}

    /**
     * Updates the available players list in the TextArea.
     */
public void updateTopPlayers() {
    try {
        List<String> topPlayers = DatabaseDaoImpl.getTopPlayers();
        Platform.runLater(() -> {
            if (topPlayers.isEmpty()) {
                top_players_area.setText("No top players found.");
            } else {
                StringBuilder topPlayersList = new StringBuilder();
                for (String player : topPlayers) {
                    topPlayersList.append(player).append("\n");
                }
                top_players_area.setText(topPlayersList.toString());
            }
        });
    } catch (Exception e) {
        Platform.runLater(() -> appendTextToArea(received_data_area, "Error fetching top players: " + e.getMessage() + "\n"));
    }
}

public void updateAvailablePlayers() {
    try {
        List<String> availablePlayers = DatabaseDaoImpl.getAvailablePlayers();
        Platform.runLater(() -> {
            if (availablePlayers.isEmpty()) {
                available_players_area.setText("No players available.");
            } else {
                StringBuilder playersList = new StringBuilder();
                for (String player : availablePlayers) {
                    playersList.append(player).append("\n");
                }
                available_players_area.setText(playersList.toString());
            }
        });
    } catch (Exception e) {
        Platform.runLater(() -> appendTextToArea(received_data_area, "Error fetching available players: " + e.getMessage() + "\n"));
    }
}

public TextArea getReceivedDataArea() {
    return received_data_area;
}
}