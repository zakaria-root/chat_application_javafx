package com.example.clientserver;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Button sendButton;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField textField;

    @FXML
    private VBox vboxMessages;


    private Client client;

    @FXML
    private Label name  ;


    @FXML
    private VBox vboxConversationNames;



    @FXML
    void onSendeMessageByKeyboard(KeyEvent keyEvent){
        if (keyEvent.getCode().toString().equals("ENTER"))
            onSendeMessage(new ActionEvent());
    }


    @FXML
    void onSendeMessage(ActionEvent event) {
        String message = textField.getText();
        if (message != null && !message.isEmpty()) {
            HBox hBox = new HBox();
            hBox.alignmentProperty().set(javafx.geometry.Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5, 5, 5, 10));
            Text text = new Text(message);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #02d102; -fx-background-radius: 5; -fx-padding: 15; -fx-border-insets: 3px; -fx-background-insets: 3px;");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(javafx.scene.paint.Color.gray(0.1));
            TextFlow textFolowTime = getTextTime();
            hBox.getChildren().add(textFolowTime);
            hBox.getChildren().add(textFlow);
            vboxMessages.getChildren().add(hBox);
            client.sendMessageToServer(message);
            textField.clear();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

       if (LoginController.client != null) {
            client = LoginController.client;
            name.setText(client.getName());
        }else{
           System.out.println("connection failed");
           return;
       }

        client.recieveMessageFromServer(vboxMessages);

        vboxMessages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                scrollPane.setVvalue((Double) t1);
            }
        });

    }


    public static void addConversationName(String name, VBox vbox){
        HBox hBox = new HBox();
        hBox.alignmentProperty().set(javafx.geometry.Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 10, 5, 5));
        Text textName = new Text("@"+name + " ");
        TextFlow textFlowName = new TextFlow(textName);
        textFlowName.setStyle("-fx-background-color: #6495ED; -fx-background-radius: 5; -fx-padding: 15; -fx-font-weight: bold; -fx-font-size: 10; -fx-fill: #808080 ; -fx-padding: 5; -fx-background-radius: 5; -fx-text-alignment: center;");
        textName.setFill(Color.GRAY);
        hBox.getChildren().add(textFlowName);
        vbox.getChildren().add(hBox);
    }

    public static void addLabel(String messageFromClient, VBox vbox) {

        if (messageFromClient == null || messageFromClient.isEmpty())
        return;
        if (messageFromClient.contains("Enter your name:")){
            return;
        }

        HBox hBox = new HBox();
        hBox.alignmentProperty().set(javafx.geometry.Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 10, 5, 5));

        String name = messageFromClient.split(":")[0].trim();
        Text textName = new Text("@"+name + " ");

        TextFlow textFlowName = new TextFlow(textName);
        if (name.equals("server")){
            textFlowName.setStyle("-fx-font-weight:bold; -fx-font-size: 10; -fx-fill: #808080 ; -fx-padding: 5; -fx-background-radius: 5; -fx-text-alignment: center;");
            textName.setFill(Color.GRAY);
        }
        else{
            textFlowName.setStyle("-fx-background-color: #6495ED; -fx-background-radius: 5; -fx-padding: 15; -fx-font-weight: bold; -fx-color: #FFFFFF;  -fx-border-insets: 3px;" +
                    "    -fx-background-insets: 3px;");
        textName.setFill(Color.WHITE);
        }

        Text text = new Text(messageFromClient.split(":")[1].trim());

        TextFlow textFlow = new TextFlow(text);
        if (name.equals("server"))
            textFlow.setStyle(" -fx-font-size: 11; -fx-fill: #808080 ; -fx-padding: 5; -fx-background-radius: 5; -fx-text-alignment: center;");
        else
        textFlow.setStyle("-fx-background-color: #6495ED; -fx-background-radius: 5; -fx-padding: 15;-fx-border-insets: 3px; -fx-background-insets: 3px;  ");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        if (name.equals("server"))
            text.setFill(Color.GRAY);
        else
            text.setFill(Color.BLACK);

        TextFlow textFolowTime = getTextTime();

        hBox.getChildren().add(textFlowName);
        hBox.getChildren().add(textFlow);
        hBox.getChildren().add(textFolowTime);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hBox);
            }
        });
    }

    private static TextFlow getTextTime() {
        DateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa");
        String dateString2 = dateFormat2.format(new Date()).toString();
        Text textTime = new Text(" " + dateString2);
        textTime.setFill(Color.GRAY);
        textTime.fontProperty().set(Font.font("Arial", FontPosture.ITALIC, 9));
        TextFlow textFlowTime = new TextFlow(textTime);
        textFlowTime.setStyle(" -fx-font-size: 9; -fx-fill: #808080 ; -fx-padding: 30 10 0 0 ; -fx-background-radius: 5; ");
        textFlowTime.setPadding(new Insets(5, 10, 5, 10));
        return textFlowTime;
    }


}

