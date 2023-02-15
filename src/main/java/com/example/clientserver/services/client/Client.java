package com.example.clientserver.services.client;

import com.example.clientserver.controllers.HelloController;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private String name;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.name = null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in creating client");
            closeEvreything(socket, out, in);
        }
    }


    public void sendMessageToServer(String message) {
        try {
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in sending message to server");
            closeEvreything(socket, out, in);
        }
    }

    public void recieveMessageFromServer(VBox vbox) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = in.readLine();
                        if (message.equals("Enter your name"))
                            sendMessageToServer(name);
                        else
                            HelloController.addLabel(message, vbox);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error in recieving message from server");
                        closeEvreything(socket, out, in);
                        break;
                    }
                }
            }
        }).start();
    }

    public void closeEvreything(Socket socket, BufferedWriter out, BufferedReader in) {
        try {
            if (in != null) {
                in.close();

            } else if (out != null) {
                out.close();
            } else if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void joinWithNameToServer(String text) {
        sendMessageToServer(text);
        name = text + " Online";
    }

    public String getName() {
        return name;
    }
}

