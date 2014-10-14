package States.Reciver;

import Audio.AudioStreamUDP;
import States.BusyState;
import States.Conversation;
import States.State;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Naknut on 14/10/14.
 */
public class WaitAck extends BusyState {

    AudioStreamUDP stream;
    String sipName;
    InetAddress remoteAddress;
    int remotePort;

    public WaitAck(AudioStreamUDP stream, String sipName, InetAddress remoteAddress, int remotePort) {
        this.stream = stream;
        this.sipName = sipName;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("ACK")) {
            return new Conversation(socket, stream, remoteAddress, remotePort, sipName);
        } else if(input.startsWith("INVITE"))
            sendBusy(socket);
        return this;
    }

    @Override
    public State handleUserInput(String input) {
        return this;
    }
}
