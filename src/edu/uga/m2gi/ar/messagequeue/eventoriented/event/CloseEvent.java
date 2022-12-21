package edu.uga.m2gi.ar.messagequeue.eventoriented.event;

import edu.uga.m2gi.ar.messagequeue.eventoriented.Cookie;

public class CloseEvent extends Event {
    private final Cookie cookie;

    public CloseEvent(Cookie cookie) {
        this.cookie = cookie;
    }

    public Cookie getCookie() {
        return this.cookie;
    }

    @Override
    public EventType getType() {
        return EventType.CLOSE;
    }
}
