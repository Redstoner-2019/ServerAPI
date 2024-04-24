package me.redstoner2019.defaultpackets;

import me.redstoner2019.util.ConnectionProtocol;

public class ConnectRequestPacket extends Packet {
    private ConnectionProtocol protocol;

    public ConnectionProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(ConnectionProtocol protocol) {
        this.protocol = protocol;
    }

    public ConnectRequestPacket(ConnectionProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "ConnectRequestPacket{" +
                "protocol=" + protocol +
                '}';
    }
}
