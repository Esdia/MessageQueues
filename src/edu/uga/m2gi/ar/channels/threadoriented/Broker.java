package edu.uga.m2gi.ar.channels.threadoriented;

import java.util.HashMap;
import java.util.Map;

public class Broker {
    private final String m_name;
    private final Map<Integer, Channel> channels = new HashMap<>();

    public Broker(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }


    void releasePort(int port) {
        // remove(port) would use port as an index to remove, not a value, hence the cast to Integer
        this.channels.remove(port);
    }

    public Channel accept(int port) {
        if (this.channels.containsKey(port)) {
            if (this.channels.get(port).disconnected()) {
                this.releasePort(port);
            } else {
                throw new IllegalStateException("Port " + port + " already in use");
            }
        }

        String key = this.m_name + "_" + port;
        RendezVous rdv = RendezVous.getRdv(key);
        Channel channel = new ChannelImpl(this, port, key, rdv.getAcceptIn(), rdv.getAcceptOut());
        this.channels.put(port, channel);
        return channel;
    }

    public Channel connect(String name, int port) {
        String key = name + "_" + port;
        RendezVous rdv = RendezVous.getRdv(key);
        return new ChannelImpl(key, rdv.getConnectIn(), rdv.getConnectOut());
    }
}
