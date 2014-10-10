package States;

import Audio.AudioStreamUDP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Naknut on 10/10/14.
 */
public class Ringing extends BusyState {

    AudioStreamUDP stream;
    Boolean okSent = false;
    int remotePort;

    public Ringing(Socket socket, String sipFrom, int remotePort) {
        this.remotePort = remotePort;
        System.out.println("Call from " + sipFrom);
        System.out.println("Pick up? Y/N");
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("180 RINGING");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.equals("Y")) {
                stream = new AudioStreamUDP();
                out.println("200 OK " + stream.getLocalPort());
                okSent = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(okSent && input.equals("ACK")) {
            InetAddress remoteAddress = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
            return new Conversation(socket, stream, remoteAddress, remotePort);
        }
        return this;
    }
}
