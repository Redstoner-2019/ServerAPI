package me.redstoner2019.events;

import me.redstoner2019.odserver.ClientHandler;

public interface PacketReadFailedEvent {
    void onPacketReadFailed(String error, ClientHandler handler);
}
