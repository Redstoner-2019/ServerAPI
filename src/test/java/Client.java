import me.redstoner2019.util.ConnectionProtocol;
import me.redstoner2019.odclient.ODClient;
import me.redstoner2019.events.ConnectionFailedEvent;
import me.redstoner2019.events.ConnectionLostEvent;
import me.redstoner2019.events.ConnectionSuccessEvent;
import me.redstoner2019.events.PacketListener;

public class Client extends ODClient {
    public static void main(String[] args) {
        setConnectionFailedEvent(new ConnectionFailedEvent() {
            @Override
            public void onConnectionFailedEvent(Exception reason) {
                System.out.println("Connection Failed: " + reason);
            }
        });
        setConnectionLostEvent(new ConnectionLostEvent() {
            @Override
            public void onConnectionLostEvent(String reason) {
                System.out.println("Connection Lost: " + reason);
            }
        });
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object packet) {
                System.out.println(packet + " recieved");
            }
        });
        setOnConnectionSuccessEvent(new ConnectionSuccessEvent() {
            @Override
            public void onConnectionSuccess() {
                System.out.println("Connection success");
            }
        });
        startSender();
        connect("localhost",8007, ConnectionProtocol.TCP);
        sendObject(new MessagePacket("Hello World"));
    }
}
