package com.example.clientserver.servers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerMultithread extends Thread {
    private int id;
    private static List<Conversation> conversations = new ArrayList<>();

    public ServerMultithread() {
        id = 0;
    }

    public static void main(String[] args) {
        new ServerMultithread().start();
    }

    @Override
    public void run() {
        String name =null;
        try {

            ServerSocket ss = new ServerSocket(1234);
            Socket s = null;
            while (true) {
                ++id;
                s = ss.accept();

            conversations.add(new Conversation(s, id, name));
            new Conversation(s, id, name).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    class Conversation extends Thread {
        private int id;
        private Socket s;
        private String name;

        public Conversation(Socket s, int id, String name ) throws IOException {
            this.s = s;
            this.id = id;
            this.name = name;

        }

        @Override
        public void run() {
            try {
                InputStream is = s.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                OutputStream os = s.getOutputStream();
                PrintWriter pr = new PrintWriter(os, true);
                if (this.name == null) {
                    pr.println("Enter your name");
                    this.name = br.readLine();
                    String line = this.name+" has joined the chat";
                    broadcastMessage(line,id, "server");
                }
                    String line;
                    while ((line = br.readLine()) != null) {
                        broadcastMessage(line,id, name);
                    }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }


    private void broadcastMessage(String line, int  myChannelId, String name) {
            String message = line;
            List<Integer> channelIds = new ArrayList<>();
            try {
                if (line.contains("=>")) {
                    String[] parts = line.split("=>");
                    String channel = parts[0];
                    message = parts[1];
                    if (channel.contains(",")) {
                        String[] channels = channel.split(",");
                        channelIds.addAll(Arrays.stream(channels).map(Integer::parseInt).collect(Collectors.toList()));
                    } else {
                        channelIds.add(Integer.parseInt(channel));
                    }
                } else
                    channelIds= conversations.stream().map(conversation -> conversation.id).collect(Collectors.toList());

                for (Conversation conversation : conversations) {
                    for (int channelId : channelIds) {
                        if (conversation.id == channelId && conversation.id != myChannelId) {
                            OutputStream os = conversation.s.getOutputStream();
                            PrintWriter pr = new PrintWriter(os, true);
                            pr.println(name+ ": " + message);
                            System.out.println(message +" from " + name);
                        }
                    }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
            }
        }
    }