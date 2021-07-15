package com.stk123.common.util.chat;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;

public class ChatClient {

    private Client client;
    private String name;
    private String serverIp;

    public ChatClient (String serverIp) throws IOException {
        this.serverIp = serverIp;
        client = new Client();
        client.start();

        // For consistency, the classes to be sent over the network are
        // registered by the same method for both the client and server.
        Network.register(client);

        client.addListener(new Listener() {
            public void connected (Connection connection) {
                System.out.println("ChatServer connected.");
            }

            public void received (Connection connection, Object object) {
                if (object instanceof Network.ChatMessage) {
                    Network.ChatMessage chatMessage = (Network.ChatMessage)object;
                    System.out.println(chatMessage.text);
                    chatMessage.text += " OK";
                    client.sendTCP(chatMessage);
                    return;
                }
            }

            public void disconnected (Connection connection) {
                //connection.close();
                System.out.println("ChatServer discount...");
                while(true) {
                    try {
                        Thread.sleep(10000);
                        client.reconnect();
                        client.run();
                        break;
                    } catch (Exception e) {
                        System.out.println("Cannot reconnect to ChatServer.");
                    }
                }
            }
        });

        new Thread("Connect") {
            public void run () {
                try {
                    client.connect(5000, serverIp, Network.port);
                    // Server communication after connection can go here, or in Listener#connected().
                    client.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }.start();


    }



    public static void main (String[] args) throws IOException {
        //Log.set(Log.LEVEL_DEBUG);
        new ChatClient("localhost");
    }

}
