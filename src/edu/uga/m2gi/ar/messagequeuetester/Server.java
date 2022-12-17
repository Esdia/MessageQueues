package edu.uga.m2gi.ar.messagequeuetester;

import edu.uga.m2gi.ar.messagequeue.MessageQueue;
import edu.uga.m2gi.ar.messagequeuetester.message.*;

import java.util.HashMap;
import java.util.Map;

public class Server extends TesterTask {
    private final Map<Integer, MessageQueue> connexions;
    private static int nextPort = Constants.SERVER_ENTRY_PORT;

    public Server() {
        super(Constants.SERVER_ID);
        connexions = new HashMap<>();
    }

    private int nextPort() {
        nextPort++;
        return nextPort;
    }

    private void closeConnection(Integer clientId) {
        synchronized (this.connexions) {
            MessageQueue queue = this.connexions.get(clientId);
            this.connexions.remove(clientId);
            queue.close();
        }
    }

    private void handleClient(Integer clientId, Integer port) {
        MessageQueue queue = this.broker.accept(port);
        synchronized (this.connexions) {
            this.connexions.put(clientId, queue);
        }
        Message message;
        while (true) {
            message = this.receiveMessage(queue);

            switch (message.getMessageType()) {
                case DEFAULT -> {
                    System.out.println("Server : Received message from client n°" + message.getSenderId() + ", broadcasting to other clients");
                    NormalMessage normalMessage = NormalMessage.fromMessage(message);
                    synchronized (this.connexions) {
                        for (Map.Entry<Integer, MessageQueue> entry : this.connexions.entrySet()) {
                            if (!entry.getKey().equals(message.getSenderId())) {
                                this.sendMessage(normalMessage, entry.getValue());
                            }
                        }
                    }
                }
                case DISCONNECTION -> {
                    DisconnectionAckMessage disconnectionAck = new DisconnectionAckMessage();
                    synchronized (this.connexions) {
                        this.sendMessage(disconnectionAck, queue);
                        this.closeConnection(message.getSenderId());
                        System.out.println("Server : Client n°" + message.getSenderId() + " disconnected. Remaining clients : " + this.connexions.keySet());
                    }
                    return;
                }
                default -> {
                    System.out.println("Server : Client n°" + message.getSenderId() + " sent an illegal message type : closing connection");
                    this.closeConnection(message.getSenderId());
                    return;
                }
            }
        }
    }

    private void handleConnection(MessageQueue queue) {
        Message message = this.receiveMessage(queue);
        if (message.getMessageType() != MessageType.CONNECTION) {
            queue.close();
            return;
        }

        Integer clientId = message.getSenderId();
        System.out.println("Server : New client : id = " + clientId);

        Integer newPort = this.nextPort();
        Message response = new ConnectionInfoMessage(newPort);
        this.sendMessage(response, queue);

        Message ack = this.receiveMessage(queue);
        if (ack.getMessageType() != MessageType.CONNECTION_INFO_ACK) {
            System.out.println("Server : Client n°" + clientId + " sent unexpected message type : " + ack.getMessageType() + ", expected " + MessageType.CONNECTION_INFO_ACK);
            return;
        }
        queue.close();

        new Thread(() -> this.handleClient(clientId, newPort)).start();
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            this.handleConnection(this.broker.accept(Constants.SERVER_ENTRY_PORT));
        }
    }
}
