package States.Reciver;

import Audio.AudioStreamUDP;
import States.BusyState;
import States.State;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public class Ringing extends BusyState {

    Socket socket;
    AudioStreamUDP stream;
    Boolean okSent = false;
    int remotePort;
    String sipName;

    public Ringing(Socket socket, String sipFrom, int remotePort, String sipName) {
        this.socket=socket;
        this.remotePort = remotePort;
        System.out.println("Call from " + sipFrom);
        System.out.println("Pick up? Y/N");
        this.sipName=sipName;
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("INVITE"))
            sendBusy(socket);
        return this;
    }

    @Override
    public State handleUserInput(String input) {
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("180 RINGING");
            if(input.equals("Y")) {
                System.out.println("You picked up the phone");
                stream = new AudioStreamUDP();
                out.println("200 OK " + stream.getLocalPort());
                okSent = true;

                System.out.println("OK sent");
                InetAddress remoteAddress = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
                return new WaitAck(stream, sipName, remoteAddress, remotePort);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
