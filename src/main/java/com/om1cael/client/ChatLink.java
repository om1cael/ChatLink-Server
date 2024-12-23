package com.om1cael.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatLink {
    private String username;
    private ByteBuffer buffer;

    public static void main(String[] args) {
        new ChatLink().setupClient();
    }

    private void setupClient() {
        this.buffer = ByteBuffer.allocate(1024);

        try(SocketChannel serverChannel = SocketChannel.open();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Scanner scanner = new Scanner(System.in)
        ) {
            this.setupUsername(scanner);
            serverChannel.connect(new InetSocketAddress(1024));

            executorService.execute(() -> this.receiveMessages(serverChannel));

            while(true) {
                this.handleMessageInput(scanner, serverChannel);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while connecting to the server.");
            System.exit(1);
        }
    }

    private void setupUsername(Scanner scanner) {
        String username;

        do {
            System.out.print("Name: ");
            username = scanner.nextLine();
        } while (username.isEmpty());

        this.username = username;
    }

    private void handleMessageInput(Scanner scanner, SocketChannel serverChannel) {
        try {
            String inputMessage = scanner.nextLine();

            String formattedMessage = username + ": " + inputMessage;
            buffer.clear().put(formattedMessage.getBytes()).flip();

            while(buffer.hasRemaining()) {
                serverChannel.write(buffer);
            }

            buffer.clear();
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveMessages(SocketChannel serverChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while(true) {
            try {
                int messageLength = serverChannel.read(buffer);

                if(messageLength == -1) {
                    serverChannel.close();
                } else {
                    System.out.print(new String(buffer.array(), 0, messageLength));
                    System.out.println();
                }

                buffer.clear();
            } catch (IOException e) {
                System.err.println("The connection has been closed.");
                System.exit(1);
            }
        }
    }
}
