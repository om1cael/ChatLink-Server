package com.om1cael.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatLinkServer {

    private static final Logger LOGGER = LogManager.getLogger(ChatLinkServer.class);
    private ByteBuffer buffer;

    private final ConcurrentHashMap<UUID, SocketChannel> clientList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<SocketChannel, SocketChannel> chatList = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        new ChatLinkServer().startServer();
    }

    private void startServer() {
        this.buffer = ByteBuffer.allocate(1024);

        try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open()
        ) {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(1024));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            LOGGER.info("Server running at port 1024");

            while(true) {
                if(selector.select() == 0) continue;

                for(SelectionKey key : selector.selectedKeys()) {
                    if(key.isAcceptable()) {
                        this.handleConnection(key, selector);
                    }

                    if(key.isReadable()) {
                        if(key.channel() instanceof SocketChannel client) {
                            this.handleRegister(client);
                            this.handleMessaging(client);
                        }
                    }
                }

                selector.selectedKeys().clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnection(SelectionKey key, Selector selector) {
        if(key.channel() instanceof ServerSocketChannel serverChannel) {
            try {
                SocketChannel client = serverChannel.accept();
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                LOGGER.info("New client connected from {}", client.getRemoteAddress());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleRegister(SocketChannel client) throws IOException {
        if(clientList.containsValue(client)) return;

        int messageLength = client.read(buffer);
        buffer.flip();

        UUID clientUUID = UUID.fromString(new String(buffer.array(), 0, messageLength));
        clientList.put(clientUUID, client);
    }

    private void handleMessaging(SocketChannel clientChannel) throws IOException {
        int messageLength = clientChannel.read(buffer);
        buffer.flip();

        String content = new String(buffer.array(), 0, messageLength);

        if(this.chatList.containsKey(clientChannel) || this.chatList.containsValue(clientChannel)) {
            this.handlePrivateChatMessages(clientChannel);
        } else if(content.startsWith("/") && !(this.chatList.containsValue(clientChannel))) {
            String targetUUIDRaw = new String(buffer.array(), 0, messageLength);
            UUID targetUUID = UUID.fromString(targetUUIDRaw.split(" ")[1]);

            this.createPrivateChat(clientChannel, targetUUID);
        } else {
            this.sendGlobalMessage();
        }

        buffer.clear();
    }

    private void createPrivateChat(SocketChannel clientChannel, UUID targetUUID) {
        if(this.clientList.containsKey(targetUUID)) {
            SocketChannel targetClientChannel = this.clientList.get(targetUUID);
            this.chatList.put(clientChannel, targetClientChannel);

            String joinedChatMessage = String.format("You are now in a private chat with %s", targetUUID);
            buffer.clear().put(joinedChatMessage.getBytes()).flip();

            chatList.forEach(this::sendPrivateChatMessage);

            LOGGER.info("Creating a new private chat");
        }
    }

    private void handlePrivateChatMessages(SocketChannel clientChannel) throws IOException {
        this.chatList.forEach((client, targetClient) -> {
            if(client != clientChannel && targetClient != clientChannel) return;

            if(client.isOpen() && targetClient.isOpen()) {
                sendPrivateChatMessage(client, targetClient);
            }
        });
    }

    private void sendPrivateChatMessage(SocketChannel client, SocketChannel targetClient) {
        sendMessage(client);
        buffer.rewind();
        sendMessage(targetClient);
    }

    private void sendMessage(SocketChannel client) {
        while(buffer.hasRemaining()) {
            try {
                client.write(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendGlobalMessage() {
        List<SocketChannel> globalClients = this.clientList.values().stream()
                .filter(client -> !(this.chatList.containsKey(client) || this.chatList.containsValue(client)))
                .toList();

        for(SocketChannel client : globalClients) {
            sendMessage(client);
            buffer.rewind();
        }
    }
}