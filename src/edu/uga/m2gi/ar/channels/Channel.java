package edu.uga.m2gi.ar.channels;

public class Channel {
    private final Broker broker;
    private final Integer port;
    private final String rdvKey;
    private final CircularBuffer in;
    private final CircularBuffer out;

    public Channel(Broker broker, Integer port, String rdvKey, CircularBuffer in, CircularBuffer out) {
        this.broker = broker;
        this.port = port;
        this.rdvKey = rdvKey;
        this.in = in;
        this.out = out;
    }

    public Channel(String rdvKey, CircularBuffer in, CircularBuffer out) {
        this(null, 0, rdvKey, in, out);
    }

    public int read(byte[] bytes, int offset, int length) {
        if (this.disconnected()) {
            return -1;
        }

        if (offset + length > bytes.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        synchronized (this.in) {
            try {
                while (this.in.empty()) {
                    this.wait();
                }

                boolean wasFull = this.in.full();

                int i;
                for (i = 0; i < length; i++) {
                    bytes[i + offset] = this.in.pull();
                    if (this.in.empty()) {
                        i++;
                        break;
                    }
                }

                if (wasFull) {
                    this.notify(); // In case the other was waiting to write
                }
                return i;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int write(byte[] bytes, int offset, int length) {
        if (this.disconnected()) {
            return -1;
        }

        if (offset + length > bytes.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        synchronized (this.out) {
            try {
                while (this.out.full()) {
                    this.wait();
                }

                boolean wasEmpty = this.out.empty();

                int i;
                for (i = 0; i < length; i++) {
                    this.out.push(bytes[offset + i]);
                    if (this.out.full()) {
                        i++;
                        break;
                    }
                }

                if (wasEmpty) {
                    this.notify(); // In case the other was waiting to read
                }

                return i;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void disconnect() {
        RendezVous.deleteInstance(this.rdvKey);
        if (this.broker != null) { // This channel is on the side that used accept
            this.broker.releasePort(this.port);
        }
    }

    public boolean disconnected() {
        return RendezVous.getInstance(this.rdvKey) == null;
    }
}
