package edu.uga.m2gi.ar.messagequeue.eventoriented;

import edu.uga.m2gi.ar.messagequeue.eventoriented.event.*;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Executor {
    private static Executor instance = null;
    private static final Map<String, MessageQueueInternal> messageQueues = new HashMap<>();
    private static final Queue<Event> eventQueue = new PriorityQueue<>();
    private Thread executionLoop;

    private Executor() {
    }

    static Executor getInstance() {
        if (instance == null) {
            instance = new Executor();
            instance.startLoop();
        }

        return instance;
    }

    public void startLoop() {
        if (this.executionLoop != null) {
            this.stopLoop();
        }

        this.executionLoop = new Thread(this::executeEvents);
    }

    public void stopLoop() {
        if (this.executionLoop != null) {
            this.executionLoop.interrupt();
        }
    }

    public void setMessageQueue(String key, MessageQueueInternal messageQueue) {
        synchronized (messageQueues) {
            messageQueues.put(key, messageQueue);
        }
    }

    public void putEvent(Event event) {
        synchronized (eventQueue) {
            boolean wasEmpty = eventQueue.isEmpty();
            eventQueue.add(event);
            if (wasEmpty) {
                eventQueue.notify();
            }
        }
    }

    private void executeEvents() {
        while (true) {
            this.executeNextEvent();
        }
    }

    private void executeNextEvent() {
        Event event;

        synchronized (eventQueue) {
            while (eventQueue.isEmpty()) {
                try {
                    eventQueue.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            event = eventQueue.remove();
        }

        switch (event.getType()) {
            case BIND -> new Thread(() -> handleBindEvent((BindEvent) event)).start();
            case CONNECT -> new Thread(() -> handleConnectEvent((ConnectEvent) event)).start();
            case CLOSE -> new Thread(() -> handleCloseEvent((CloseEvent) event)).start();
            case SEND -> new Thread(() -> handleSendEvent((SendEvent) event)).start();
            case RECEIVE -> new Thread(() -> this.handleReceiveEvent((ReceiveEvent) event)).start();
        }
    }

    private void handleBindEvent(BindEvent event) {
        int port = event.getPort();
        MessageQueue queue = event.getBroker().acceptInternal(port);
        event.getAcceptListener().accepted(port, queue);
    }

    private void handleConnectEvent(ConnectEvent event) {
        String name = event.getName();
        int port = event.getPort();
        MessageQueue queue = event.getBroker().connectInternal(name, port);
        event.getListener().connected(name, port, queue);
    }

    private void handleCloseEvent(CloseEvent event) {
        String srcKey = event.getCookie().srcKey();
        String destKey = event.getCookie().destKey();
        MessageQueueInternal queue = messageQueues.get(srcKey);
        MessageQueueInternal otherQueue = messageQueues.get(destKey);

        queue.closeInternal();
        queue.getListener().closed();
        otherQueue.getListener().closed();
    }

    private void handleSendEvent(SendEvent event) {
        MessageQueueInternal queue = event.getQueue();
        byte[] bytes = event.getMessage();
        int offset = event.getOffset();
        int length = event.getLength();

        try {
            putEvent(new ReceiveEvent(event.getCookie()));
            queue.sendInternal(bytes, offset, length);
            queue.getListener().sent(bytes, offset, length);
        } catch (QueueClosedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleReceiveEvent(ReceiveEvent event) {
        String key = event.getCookie().destKey();
        MessageQueueInternal queue = messageQueues.get(key);

        try {
            byte[] message = queue.receiveInternal();
            queue.getListener().received(message);
        } catch (QueueClosedException e) {
            throw new RuntimeException(e);
        }
    }
}
