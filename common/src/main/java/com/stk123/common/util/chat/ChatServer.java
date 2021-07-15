package com.stk123.common.util.chat;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.stk123.common.util.chat.Network.ChatMessage;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class ChatServer {

    private Server server;

    private static Map<String, CountDownLatch> latch = new HashMap<>();
    private static Map<String, ChatMessage> clientMessageMap = new HashMap<>();

    public ChatServer() throws Exception {
        server = new Server() {
            protected Connection newConnection () {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new ChatConnection();
            }
        };

        // For consistency, the classes to be sent over the network are
        // registered by the same method for both the client and server.
        Network.register(server);

        server.addListener(new Listener() {

            public void received (Connection c, Object object) {
                if (object instanceof ChatMessage) {
                    // Ignore the object if a client tries to chat before registering a name.
                    //if (connection.name == null) return;
                    ChatMessage chatMessage = (ChatMessage)object;
                    String id = chatMessage.id;
                    clientMessageMap.put(id, chatMessage);
                    latch.get(id).countDown();
                }
            }

            public void disconnected (Connection c) {
                ChatConnection connection = (ChatConnection)c;
                if (connection.name != null) {
                    // Announce to everyone that someone (with a registered name) has left.
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.text = connection.name + " disconnected.";
                    server.sendToAllTCP(chatMessage);
                }
            }
        });
        server.bind(Network.port);
        server.start();
        System.out.println("ChatServer started.");
/*
        while(true){
            sendMessage(new Date() + " connected.", 3);
            Thread.sleep(3000L);
        }*/
    }

    public String sendMessage(String message, int timeout) throws Exception {
        ChatMessage chatMessage = new ChatMessage();
        String uuid = UUID.randomUUID().toString();
        chatMessage.id = uuid;
        chatMessage.text = message;

        CountDownLatch cdl = new CountDownLatch(1);
        latch.put(uuid, cdl);

        //System.out.println("[Send]"+chatMessage.text);
        server.sendToAllTCP(chatMessage);

        if(cdl.await(timeout, TimeUnit.SECONDS)) {
            ChatMessage returnChatMessage = clientMessageMap.get(uuid);
            //System.out.println("[Return]" + returnChatMessage.text);
            return returnChatMessage.text;
        }
        latch.remove(uuid);
        return null;
    }

    // This holds per connection state.
    static class ChatConnection extends Connection {
        public String name;
    }

    public static void main (String[] args) throws Exception {
        //Log.set(Log.LEVEL_DEBUG);
        new ChatServer();
    }
}
