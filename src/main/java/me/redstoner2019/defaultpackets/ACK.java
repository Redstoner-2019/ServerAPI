package me.redstoner2019.defaultpackets;

public class ACK extends Packet {
    private String uuid;
    private long checksum;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public long getChecksum() {
        return checksum;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

    public ACK(String uuid, long checksum) {
        this.uuid = uuid;
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return "ACK{" +
                "uuid='" + uuid + '\'' +
                ", checksum=" + checksum +
                '}';
    }
}
