package States;

import Audio.AudioStreamUDP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

/**
 * Created by Naknut on 10/10/14.
 */
public class Conversation extends BusyState {

    Scanner scanner;
    Boolean byeSent = false;
    AudioStreamUDP stream;

    public Conversation(Socket socket, AudioStreamUDP stream, InetAddress remoteAddress, int remotePort) {
        this.stream = stream;
        try {
            this.stream.connectTo(remoteAddress, remotePort);
            this.stream.startStreaming();
            System.out.print("Press ENTER to hang up.");
            scanner.nextLine();
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println("BYE");
            byeSent = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("BYE")) {
            scanner.close();
            stream.close();
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("200 OK");
                return new Idle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(input.startsWith("200 OK") && byeSent) {
            try {
                socket.close();
                scanner.close();
                stream.close();
                return new Idle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(input.startsWith("INVITE")) {
            sendBusy(socket);
        }
        return this;
    }
}
