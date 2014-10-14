package States.Caller;

import Audio.AudioStreamUDP;
import States.BusyState;
import States.Conversation;
import States.Reciver.Ringing;
import States.State;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public class WaitRinging extends BusyState {

    AudioStreamUDP stream;
    String sipName;

    public WaitRinging(AudioStreamUDP stream, String sipName) {
        this.sipName=sipName;
        this.stream = stream;
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("200 OK")) {
            String[] tokens = input.split(" ");
            InetAddress remoteAddress = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
            PrintWriter out;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("ACK");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new Conversation(socket, stream, remoteAddress, Integer.parseInt(tokens[2]),sipName);
        }
        else if(input.startsWith("INVITE"))
            sendBusy(socket);
        return this;
    }

    @Override
    public State handleUserInput(String input) {
        return this;
    }
}
