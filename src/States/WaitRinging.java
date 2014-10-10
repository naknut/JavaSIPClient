package States;

import Audio.AudioStreamUDP;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public class WaitRinging extends BusyState {

    AudioStreamUDP stream;

    public WaitRinging(AudioStreamUDP stream) {
        this.stream = stream;
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("200 OK")) {
            String[] tokens = input.split(" ");
            InetAddress remoteAddress = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
            return new Conversation(socket, stream, remoteAddress, Integer.parseInt(tokens[2]));
        }
        else if(input.startsWith("INVITE"))
            sendBusy(socket);
        return this;
    }
}
