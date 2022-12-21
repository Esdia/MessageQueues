package edu.uga.m2gi.ar.messagequeue.eventoriented;

import edu.uga.m2gi.ar.messagequeue.eventoriented.listener.MessageListener;

public interface MessageQueueInternal extends MessageQueue {
    MessageListener getListener();
    void sendInternal(byte[] bytes, int offset, int length) throws QueueClosedException;
    byte[] receiveInternal() throws QueueClosedException;
    void closeInternal();
}
