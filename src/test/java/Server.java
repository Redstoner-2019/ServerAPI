import me.redstoner2019.odserver.ClientHandler;
import me.redstoner2019.util.ConnectionProtocol;
import me.redstoner2019.odserver.ODServer;
import me.redstoner2019.events.ClientConnectEvent;
import me.redstoner2019.events.PacketListener;

public class Server extends ODServer {
    public static void main(String[] args) {
        setup(8007, ConnectionProtocol.TCP);
        start();
    }
}
