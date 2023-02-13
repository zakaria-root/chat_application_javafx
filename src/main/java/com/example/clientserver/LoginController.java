package com.example.clientserver;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class LoginController {
    static  Client client;
    @FXML
    private Button joinButton;

    @FXML
    private TextField loginField;

    @FXML
    void onJoinByKeyboard(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER"))
        onJoin(new ActionEvent());
    }

    @FXML
    void onJoin(ActionEvent event)  {
        try {
            client = new Client(new Socket("localhost", 1234));
        } catch (IOException e) {
            System.out.println("connection failed");
            e.printStackTrace();
            return;
        }

        if (!loginField.getText().isEmpty()) {
            client.joinWithNameToServer(loginField.getText());

            loginField.clear();
            Stage stageCrr = (Stage) joinButton.getScene().getWindow();
            stageCrr.close();
            Stage stage = new Stage();
            stage.setTitle("My Chat App");
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            try {
                stage.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.show();
        }
    }
}
