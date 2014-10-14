package States;

import Audio.AudioStreamUDP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public class Conversation extends BusyState {

    Socket socket;
    String sipName;
    Boolean byeSent = false;
    AudioStreamUDP stream;

    public Conversation(Socket socket, AudioStreamUDP stream, InetAddress remoteAddress, int remotePort, String sipName) {
        this.stream = stream;
        this.socket=socket;
        try {
            this.stream.connectTo(remoteAddress, remotePort);
            this.stream.startStreaming();
            System.out.print("Press ENTER to hang up.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sipName=sipName;
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("BYE")) {
            stream.close();
            PrintWriter out = null;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("200 OK");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Idle(sipName);
        } else if(input.startsWith("200 OK") && byeSent) {
            try {
                socket.close();
                stream.close();
                return new Idle(sipName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(input.startsWith("INVITE")) {
            sendBusy(socket);
        }
        return this;
    }

    @Override
    public State handleUserInput(String input) {

        if((input.startsWith("END"))){
            PrintWriter out = null;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("BYE");
                byeSent = true;
                System.out.println("Bye sent");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new HangingUp(stream, sipName);
        }
        return this;
    }
}
