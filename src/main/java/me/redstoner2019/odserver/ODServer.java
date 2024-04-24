package me.redstoner2019.odserver;

import me.redstoner2019.defaultpackets.ACK;
import me.redstoner2019.util.ConnectionProtocol;
import me.redstoner2019.util.Util;
import me.redstoner2019.defaultpackets.ConnectRequestPacket;
import me.redstoner2019.defaultpackets.ConnectSuccessPacket;
import me.redstoner2019.defaultpackets.ConnectionRejectedPacket;
import me.redstoner2019.events.ClientConnectEvent;
import me.redstoner2019.events.PacketReadFailedEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static me.redstoner2019.util.ConnectionProtocol.TCP;

public class ODServer {
    private static int PORT = 3030;
    private static ClientConnectEvent clientConnectEvent = handler -> {};
    private static PacketReadFailedEvent packetReadFailedEvent = (error, handler) -> {};
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static ConnectionProtocol protocol;
    public static ConnectionProtocol getProtocol(){
        return protocol;
    }

    public static java.util.List<ClientHandler> getClients() {
        return clients;
    }

    public static void setup(int port, ConnectionProtocol connProtocol){
        PORT = port;
        protocol = connProtocol;
    }
    public static void start(){
        try {
            Util.log("Starting Server on Port " + InetAddress.getLocalHost().getHostAddress() + ":" +  PORT);
            Util.log("Using protocol: " + protocol);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            Util.log("Couldn't start Server: " + e.getLocalizedMessage());
            return;
        }
        while (serverSocket.isBound()){
            try{
                final Socket socket = serverSocket.accept();
                final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ClientHandler handler = new ClientHandler(in,out,socket);
                            Object o = in.readObject();
                            if(o instanceof ConnectRequestPacket p){
                                if(p.getProtocol().equals(protocol)){
                                    Util.log("Login validation complete");
                                    clients.add(handler);
                                    if(ODServer.getProtocol().equals(TCP)) handler.sendObject(new ACK(p.uuid,0));
                                    handler.sendObject(new ConnectSuccessPacket());
                                    clientConnectEvent.connectEvent(handler);
                                    return;
                                } else {
                                    Util.log("Incorrect Protocol " + p.getProtocol().name());
                                    handler.sendObject(new ConnectionRejectedPacket("Incorrect Protocol"));
                                }
                            } else {
                                Util.log("Invalid login Request");
                                handler.sendObject(new ConnectionRejectedPacket("Invalid login request"));
                            }
                            handler.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }catch (Exception ignored){
                ignored.printStackTrace();
            }
        }
    }

    public static void setClientConnectEvent(ClientConnectEvent e){
        clientConnectEvent = e;
    }
    public static void setPacketReadFailedEvent(PacketReadFailedEvent e){
        packetReadFailedEvent = e;
    }
    public static void sendObject(Object packet, ObjectOutputStream out, Socket socket){
        if(!socket.isConnected()){
            Util.log("Not Connected, can't send Object.");
            return;
        }
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            Util.log("Error sending Object.");
        }
    }
    public static void broadcastMessage(Object packet){
        for(ClientHandler c : clients){
            c.sendObject(packet);
        }
    }
    public static void broadcastMessage(Object packet, List<ClientHandler> exceptions){
        Util.log(clients.size() + " Broadcasting " + packet);
        for(ClientHandler c : clients){
            if(exceptions.contains(c)) continue;
            c.sendObject(packet);
        }
    }
}