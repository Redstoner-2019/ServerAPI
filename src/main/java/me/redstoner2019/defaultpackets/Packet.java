package me.redstoner2019.defaultpackets;

import java.io.Serial;
import java.io.Serializable;

public class Packet implements Serializable {
    @Serial
    private static final long serialVersionUID = -6849794470754667710L;
    public String uuid = null;
    public long getChecksum(){
        return 0;
    }
}
