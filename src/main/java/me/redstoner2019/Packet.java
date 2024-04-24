package me.redstoner2019.serverhandling;

import me.redstoner2019.main.Main;

import java.io.Serial;
import java.io.Serializable;

public class Packet implements Serializable {
    @Serial
    private static final long serialVersionUID = -6849794470754667710L;
    private String version = Main.getVersion();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
