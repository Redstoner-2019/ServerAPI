package me.redstoner2019.events;

import me.redstoner2019.odserver.ClientHandler;

public interface ClientConnectEvent {
    void connectEvent(ClientHandler handler) throws Exception;
}
