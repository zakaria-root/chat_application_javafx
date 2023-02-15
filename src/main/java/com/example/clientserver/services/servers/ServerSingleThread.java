package com.example.clientserver.services.servers;

import java.io.IOException;
        import java.net.InetSocketAddress;
        import java.nio.ByteBuffer;
        import java.nio.channels.*;
        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.List;
        import java.util.Set;

class ServerSingleThread extends Thread{

    private static Selector selector;
    private static  List<Conversation> conversations = new ArrayList<>();
    private static SelectionKey id ;

    public static void main(String[] args) throws IOException {

        new ServerSingleThread().start();

    }

    @Override
    public void run() {
        ServerSocketChannel serverSocket = null;
        try {
            serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        serverSocket.bind(new InetSocketAddress("localhost", 1234));
        selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                dispatch(key);
                keyIterator.remove();
            }
        }
    }

    private static void dispatch(SelectionKey key) {

        if (key.isAcceptable()) {
            handleAccept(key);
        } else if (key.isReadable()) {
            handleRead(key);
        }
    }

    private static void handleAccept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel;
        try {
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            id = key;

            ByteBuffer bufferWrited = ByteBuffer.wrap("Enter your name: \n".getBytes());
            socketChannel.write(bufferWrited);

            Conversation conversation = new Conversation(id,socketChannel);
            conversations.add(conversation);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            // handle exception
        }
    }

    private static void handleRead(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead;
        String name=null;
        try {
            bytesRead = socketChannel.read(buffer);
            if (bytesRead == -1) {
                // handle end of stream
                socketChannel.close();
            } else {
                String request = new String(buffer.array(), 0, bytesRead);
                for (Conversation conversation: conversations){

                    if (conversation.getS().equals(socketChannel)){
                        if (conversation.getName()==null){
//                            System.out.println("asigne nmae: " + request);
                            conversation.setName(request);
                            name = request;
                            broadcastMessage(name.trim()+" has joined the chat\n",socketChannel, "server");
                        }else{
                            name = conversation.getName();
                        }

                    }
                }
                if (!request.equals(name))
                    broadcastMessage(request,socketChannel, name );
            }
        } catch (IOException e) {
            // handle exception
        }
    }
    private static void broadcastMessage(String data, SocketChannel mySocketChannel, String name) {
        String message  = name.trim()+ ": " + data;
        ByteBuffer bufferWrited = ByteBuffer.wrap(message.getBytes());
        for (Conversation conversation : conversations) {
            if (!conversation.getS().equals(mySocketChannel)){
                SocketChannel socketChannel = conversation.getS();
                try {
                    socketChannel.write(bufferWrited);
                } catch (IOException e) {
                    // handle exception
                }

            }


        }
    }

    static class Conversation {
        private SelectionKey id;
        private SocketChannel s;
        private String name;

        public Conversation(SelectionKey id,SocketChannel s) {
            this.s = s;
            this.id = id;
            this.name = null;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public SelectionKey getId() {
            return id;
        }
        public SocketChannel getS() {
            return s;
        }
    }
}