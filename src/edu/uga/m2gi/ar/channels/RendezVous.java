package edu.uga.m2gi.ar.channels;

import java.util.HashMap;
import java.util.Map;

class RendezVous {
    private static final Map<String, RendezVous> objects = new HashMap<>();
    private final CircularBuffer acceptIn;
    private final CircularBuffer acceptOut;
    private final CircularBuffer connectIn;
    private final CircularBuffer connectOut;

    static RendezVous getInstance(String key) {
        return objects.get(key);
    }

    static RendezVous createInstance(String key) {
        RendezVous rdv = new RendezVous();
        objects.put(key, rdv);
        return rdv;
    }

    static void deleteInstance(String key) {
        objects.remove(key);
    }

    RendezVous() {
        this.acceptIn = new CircularBuffer(1000);
        this.acceptOut = new CircularBuffer(1000);
        this.connectIn = this.acceptOut;
        this.connectOut = this.acceptIn;
    }

    public CircularBuffer getAcceptIn() {
        return acceptIn;
    }

    public CircularBuffer getAcceptOut() {
        return acceptOut;
    }

    public CircularBuffer getConnectIn() {
        return connectIn;
    }

    public CircularBuffer getConnectOut() {
        return connectOut;
    }
}
