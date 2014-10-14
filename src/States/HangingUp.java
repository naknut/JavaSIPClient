package States;

import Audio.AudioStreamUDP;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Naknut on 14/10/14.
 */
public class HangingUp extends BusyState {

    AudioStreamUDP stream;
    String sipName;

    public HangingUp(AudioStreamUDP stream, String sipName) {
        this.stream = stream;
        this.sipName = sipName;
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("200 OK")) {
            stream.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Idle(sipName);
        }
        return this;
    }

    @Override
    public State handleUserInput(String input) {
        return this;
    }
}
