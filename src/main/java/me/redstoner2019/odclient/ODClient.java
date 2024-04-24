package me.redstoner2019.odclient;

import me.redstoner2019.defaultpackets.ACK;
import me.redstoner2019.util.ConnectionProtocol;
import me.redstoner2019.defaultpackets.Packet;
import me.redstoner2019.defaultpackets.ConnectRequestPacket;
import me.redstoner2019.events.ConnectionFailedEvent;
import me.redstoner2019.events.ConnectionLostEvent;
import me.redstoner2019.events.ConnectionSuccessEvent;
import me.redstoner2019.events.PacketListener;
import me.redstoner2019.util.Util;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import static me.redstoner2019.util.ConnectionProtocol.TCP;

public class ODClient {
    public static Socket socket = null;
    public static ObjectOutputStream out;
    public static ObjectInputStream in;
    public static PacketListener listener;
    public static boolean isConnected = false;
    public static ConnectionFailedEvent connectionFailEvent = reason -> {};
    public static ConnectionSuccessEvent connectionSuccessEvent = () -> {};
    public static ConnectionLostEvent connectionLostEvent = reason -> {};
    private static List<Object> toSend = new ArrayList<>();
    private static ConnectionProtocol protocol;
    public static HashMap<String, PacketCache> packetCache = new HashMap<>();

    public static void setConnectionLostEvent(ConnectionLostEvent connectionLostEvent) {
        ODClient.connectionLostEvent = connectionLostEvent;
    }

    public static boolean isConnected(){
        return isConnected;
    }
    public static void connect(String address, int port, ConnectionProtocol connProtocol){
        protocol = connProtocol;
        try {
            socket = new Socket(address,port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            isConnected = true;
            if(connectionSuccessEvent != null) connectionSuccessEvent.onConnectionSuccess();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (!socket.isClosed()){
                        if(in == null) continue;
                        if(listener == null){
                            continue;
                        }
                        try {
                            Object o = in.readObject();
                            if(protocol.equals(TCP)) {
                                if (o instanceof ACK p) {
                                    if (packetCache.containsKey(p.getUuid())) {
                                        packetCache.remove(p.getUuid());
                                    }
                                } else if (o instanceof Packet p) {
                                    sendObject(new ACK(p.uuid, 0));
                                    listener.packetRecievedEvent(o);
                                } else {
                                    listener.packetRecievedEvent(o);
                                }
                            } else {
                                listener.packetRecievedEvent(o);
                            }
                        } catch (ClassNotFoundException ignored){
                            System.err.println("Class not found");
                            ignored.printStackTrace();
                        } catch (ClassCastException ignored){
                            System.err.println("Couldnt cast class");
                            ignored.printStackTrace();
                        } catch (StreamCorruptedException ignored){
                            System.err.println("Stream corrupted");
                            ignored.printStackTrace();
                            try {
                                in.reset();
                            } catch (IOException e) {
                                if(connectionLostEvent != null) connectionLostEvent.onConnectionLostEvent(ignored.getLocalizedMessage());
                                break;
                            }
                        } catch (SocketException ignored){
                            if(connectionLostEvent != null) connectionLostEvent.onConnectionLostEvent(ignored.getLocalizedMessage());
                            break;
                        } catch (EOFException ignored){
                            try {
                                in.reset();
                            } catch (IOException e) {
                                connectionLostEvent.onConnectionLostEvent(e.getLocalizedMessage());
                                disconnect();
                            }
                        }catch (Exception e) {
                            System.err.println("Lukas du hurensohn was hast du getan dass dies ausgegeben wird");
                            System.err.println("Localized message: " + e.getLocalizedMessage());
                            e.printStackTrace();
                            if(connectionLostEvent != null) connectionLostEvent.onConnectionLostEvent(e.getClass() + " " + e.getLocalizedMessage());
                            try {
                                out.flush();
                            } catch (IOException ex) {

                            }
                            break;
                        }
                    }
                    disconnect();
                }
            });
            t.start();
            sendObject(new ConnectRequestPacket(protocol));
        } catch (SocketException e) {
            if(connectionFailEvent != null) connectionFailEvent.onConnectionFailedEvent(e);
            System.err.println("Couldnt connect, socket exception!");
            Util.log(e.getLocalizedMessage());
        } catch (UnknownHostException e) {
            if(connectionFailEvent != null) connectionFailEvent.onConnectionFailedEvent(e);
            System.err.println("Unknown Host");
            Util.log(e.getLocalizedMessage());
        } catch (IOException e) {
            if(connectionFailEvent != null) connectionFailEvent.onConnectionFailedEvent(e);
            e.printStackTrace();
        }
    }
    public static String lastObjectSendName = "";
    public static void sendObject(Object o){
        try {
            toSend.add(o);
        }catch (Exception e){
            Util.log("Clearing Buffer");
            toSend.clear();
        }
    }
    public static void startSender() {
        final Object REFERENCE = new Object();
        final long TIMEOUT = 2000;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    synchronized (REFERENCE){
                        if(!isConnected()) {
                            toSend.clear();
                            continue;
                        }

                        Iterator<String> uuids = packetCache.keySet().iterator();

                        if(protocol.equals(TCP)) while (uuids.hasNext()){
                            String uuid = uuids.next();
                            PacketCache pc = packetCache.get(uuid);
                            if(pc == null) continue;
                            if(System.currentTimeMillis() - pc.getSendTime() > TIMEOUT){
                                sendObject(pc.getPacket());
                                uuids.remove();
                            }
                        }

                        if(toSend.isEmpty()) continue;

                        Object o = toSend.get(0);
                        toSend.remove(0);

                        if(o == null) continue;

                        if(protocol.equals(TCP)) if(o instanceof Packet p){
                            if(!(p instanceof ACK) && p.uuid == null){
                                p.uuid = Util.createUUID();
                                packetCache.put(p.uuid,new PacketCache(System.currentTimeMillis(),p));
                            }
                        }

                        try {
                            lastObjectSendName = o.getClass().toString();
                            out.writeObject(o);
                            out.flush();
                        } catch (SocketException e){
                            connectionLostEvent.onConnectionLostEvent(e.getLocalizedMessage());
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        t.start();
    }
    public static void setPacketListener(PacketListener packetListener){
        listener = packetListener;
    }
    public static void setConnectionFailedEvent(ConnectionFailedEvent listener){
        connectionFailEvent = listener;
    }
    public static void setOnConnectionSuccessEvent(ConnectionSuccessEvent listener){
        connectionSuccessEvent = listener;
    }
    public static void disconnect(){
        try {
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int packetsInBuffer(){
        return toSend.size();
    }
}
class PacketCache{
    private long sendTime;
    private Packet packet;

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public PacketCache(long sendTime, Packet packet) {
        this.sendTime = sendTime;
        this.packet = packet;
    }
}