package tictactoeserver;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class FXMLServerBase extends AnchorPane {

    protected final Button startBtn;
    public static Server server;
    public static boolean isStarted;

    public FXMLServerBase() {

        startBtn = new Button();
        isStarted =false;

        setMaxHeight(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setPrefHeight(483.0);
        setPrefWidth(723.0);

        startBtn.setLayoutX(282.0);
        startBtn.setLayoutY(342.0);
        startBtn.setMnemonicParsing(false);
        startBtn.setOnAction(this::onStartAction);
        startBtn.setPrefHeight(65.0);
        startBtn.setPrefWidth(159.0);
        startBtn.setStyle("-fx-background-color: GREEN;");
        startBtn.setText("START");
        startBtn.setFont(new Font(26.0));

        getChildren().add(startBtn);

    }

    protected void onStartAction(ActionEvent actionEvent){
        //start the server
        if(!isStarted){
            server = new Server();
            isStarted = true;
            startBtn.setText("STOP");
            startBtn.setStyle("-fx-background-color: RED;");
        
        //stop the server    
        }else{
            server.stopServer();
            isStarted = false;
            startBtn.setText("START");
            startBtn.setStyle("-fx-background-color: GREEN;");
        }
    }

}
