package States;

import Audio.AudioStreamUDP;

import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public class Conversation extends BusyState {

    public Conversation(AudioStreamUDP stream, int remotePort) {

    }

    @Override
    public State handleInput(String input, Socket socket) {
        return null;
    }
}
