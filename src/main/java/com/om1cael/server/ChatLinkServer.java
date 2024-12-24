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

public class ChatLinkServer {

    private static final Logger LOGGER = LogManager.getLogger();
    private ByteBuffer buffer;

    private final HashMap<SocketChannel, UUID> clientList = new HashMap<>();

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
                            //this.handleEchoing();
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
        if(clientList.containsKey(client)) return;

        int messageLength = client.read(buffer);
        buffer.flip();

        UUID clientUUID = UUID.fromString(new String(buffer.array(), 0, messageLength));
        clientList.put(client, clientUUID);
    }

    /*private void handleEchoing() {
        for(SocketChannel client : clientList) {
            try {
                int messageLength = client.read(buffer);
                buffer.flip();

                if(messageLength == -1) {
                    LOGGER.info("Client {} disconnected!", client.getRemoteAddress());
                    clientList.remove(client);
                    client.close();
                    return;
                }

                for(SocketChannel clientChannel : clientList) {
                    if(clientChannel.isOpen()) {
                        buffer.rewind();
                        while(buffer.hasRemaining()) {
                            clientChannel.write(buffer);
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Client List: {}", clientList);
            } finally {
                buffer.clear();
            }
        }
    }*/
}