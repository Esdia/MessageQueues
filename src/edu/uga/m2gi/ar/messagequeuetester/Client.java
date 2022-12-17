package edu.uga.m2gi.ar.messagequeuetester;

import edu.uga.m2gi.ar.messagequeue.MessageQueue;
import edu.uga.m2gi.ar.messagequeuetester.message.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Client extends TesterTask {
    private final Map<Integer, Integer> sequencePositions;

    public Client(int taskId) {
        super(taskId);
        sequencePositions = new HashMap<>();
    }

    private MessageQueue connectToServer() {
        synchronized (Client.class) {
            MessageQueue queue = this.broker.connect(Constants.SERVER_NAME, Constants.SERVER_ENTRY_PORT);
            ConnectionMessage connectionMessage = new ConnectionMessage(this.getTaskId());
            this.sendMessage(connectionMessage, queue);

            Message message = this.receiveMessage(queue);

            if (message.getMessageType() != MessageType.CONNECTION_INFO) {
                System.out.println("Client n°" + this.getTaskId() + " : Unexpected message type '" + message.getMessageType() + "' : Exiting");
                return null;
            }
            Message ack = new ConnectionInfoAckMessage(this.getTaskId());
            this.sendMessage(ack, queue);

            ConnectionInfoMessage connectionInfoMessage = (ConnectionInfoMessage) message;
            System.out.println("Client n°" + this.getTaskId() + " : Received info from server, connecting to port " + connectionInfoMessage.getPort());
            return this.broker.connect(Constants.SERVER_NAME, connectionInfoMessage.getPort());
        }
    }

    void sendMessages(MessageQueue queue) {
        for (int sequenceNumber = 0; sequenceNumber < Constants.SEQUENCE_SIZE; sequenceNumber++) {
            NormalMessage message = new NormalMessage(this.getTaskId(), sequenceNumber);

            ByteBuffer buffer = ByteBuffer.allocate(Constants.MESSAGE_SIZE * Integer.BYTES).order(Constants.BYTE_ORDER);
            for (int i = 0; i < Constants.MESSAGE_SIZE; i++) {
                buffer.putInt(sequenceNumber + i);
            }

            message.putByes(buffer.array());
            this.sendMessage(message, queue);
        }

        DisconnectionMessage disconnectionMessage = new DisconnectionMessage(this.getTaskId());
        this.sendMessage(disconnectionMessage, queue);
    }

    boolean receiveMessages(MessageQueue queue) {
        boolean allOk = true;
        Message message = this.receiveMessage(queue);
        while (message.getMessageType() != MessageType.DISCONNECTION_ACK) {
            if (message.getMessageType() != MessageType.DEFAULT) {
                System.out.println("Client n°" + this.getTaskId() + " : Unexpected message type '" + message.getMessageType() + "' : Aborting receiving session");
                return false;
            }

            NormalMessage normalMessage = NormalMessage.fromMessage(message);
            byte[] payload = normalMessage.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(payload).order(Constants.BYTE_ORDER);

            int expectedSequenceNumber = this.sequencePositions.getOrDefault(normalMessage.getSenderId(), 0);
            int sequenceNumber = normalMessage.getSequenceNumber();
            if (expectedSequenceNumber != sequenceNumber) {
                System.out.println("Client n°" + this.getTaskId() + " : ERROR : received message n°" + sequenceNumber + " from client n°" + normalMessage.getSenderId() + ", expected message n°" + expectedSequenceNumber);
                allOk = false;
            }

            boolean ok = true;
            for (int i = 0; i < Constants.MESSAGE_SIZE; i++) {
                int n = buffer.getInt(i * 4);
                if (n != sequenceNumber + i) {
                    System.out.println("Client n°" + this.getTaskId() + " : ERROR : incorrect value " + n + " in message n°" + sequenceNumber + " from client n°" + normalMessage.getSenderId());
                    ok = false;
                }
            }

            if (ok) {
                System.out.println("Client n°" + this.getTaskId() + " : Received message n°" + sequenceNumber + " from client n°" + normalMessage.getSenderId() + " : OK");
                this.sequencePositions.put(normalMessage.getSenderId(), sequenceNumber + 1);
            } else {
                allOk = false;
            }

            message = this.receiveMessage(queue);
        }

        System.out.println("Client n°" + this.getTaskId() + " : Disconnected : OK");
        return allOk;
    }

    @Override
    public void run() {
        MessageQueue queue = this.connectToServer();
        if (queue == null) {
            return;
        }

        Thread receiver = new Thread(() -> {
            boolean ok = receiveMessages(queue);
            if (ok) {
                System.out.println("Client n°" + this.getTaskId() + " : ALL OK");
            }
        });
        Thread sender = new Thread(() -> sendMessages(queue));

        receiver.start();
        sender.start();

        try {
            receiver.join();
            sender.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Client n°" + this.getTaskId() + " : Every thread completed, shutting down");
    }
}
