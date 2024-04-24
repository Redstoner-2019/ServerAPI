package me.redstoner2019.defaultpackets;

public class ConnectionRejectedPacket extends Packet {
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ConnectionRejectedPacket(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ConnectionRejectedPacket{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
