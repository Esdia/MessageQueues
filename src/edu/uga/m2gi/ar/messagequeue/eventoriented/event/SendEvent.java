package edu.uga.m2gi.ar.messagequeue.eventoriented.event;

import edu.uga.m2gi.ar.messagequeue.eventoriented.Cookie;
import edu.uga.m2gi.ar.messagequeue.eventoriented.MessageQueueInternal;

public class SendEvent extends Event {
    private final MessageQueueInternal queue;
    private final byte[] message;
    private final int offset;
    private final int length;
    private final Cookie cookie;

    public SendEvent(MessageQueueInternal queue, byte[] message, int offset, int length, Cookie cookie) {
        this.queue = queue;
        this.message = message;
        this.offset = offset;
        this.length = length;
        this.cookie = cookie;
    }

    public MessageQueueInternal getQueue() {
        return this.queue;
    }

    public byte[] getMessage() {
        return this.message;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    public Cookie getCookie() {
        return this.cookie;
    }

    @Override
    public EventType getType() {
        return EventType.SEND;
    }
}
