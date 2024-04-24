package me.redstoner2019.events;

public interface ConnectionFailedEvent {
    void onConnectionFailedEvent(Exception reason);
}
