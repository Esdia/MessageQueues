package edu.uga.m2gi.ar.messagequeue.threadoriented.messagequeuetester;

import edu.uga.m2gi.ar.messagequeue.threadoriented.MessageQueue;
import edu.uga.m2gi.ar.messagequeue.threadoriented.QueueBroker;
import edu.uga.m2gi.ar.messagequeue.threadoriented.QueueClosedException;
import edu.uga.m2gi.ar.messagequeue.threadoriented.Task;
import edu.uga.m2gi.ar.messagequeue.threadoriented.messagequeuetester.message.Message;

import java.io.IOException;

public abstract class TesterTask extends Task {
    private final int taskId;

    public TesterTask(int taskId) {
        super(new QueueBroker(Utils.idToName(taskId)));
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    protected synchronized void sendMessage(Message message, MessageQueue queue) {
        try {
            byte[] bytes = message.toByteArray();
            int length = bytes.length;
            queue.send(bytes, 0, length);
        } catch (IOException | QueueClosedException e) {
            System.err.println("ERROR : Exception thrown during send");
            System.exit(1);
        }
    }

    protected Message receiveMessage(MessageQueue queue) {
        try {
            byte[] bytes = queue.receive();
            return Message.fromByteArray(bytes);
        } catch (QueueClosedException | IOException e) {
            System.err.println("ERROR : Exception thrown during send");
            System.exit(1);
        }

        // To please the compiler. We should never get there since both branches of the try/catch return
        return null;
    }
}
