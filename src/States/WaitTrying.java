package States;

import Audio.AudioStreamUDP;

import java.net.Socket;


/**
 * Created by Naknut on 10/10/14.
 */
public class WaitTrying extends BusyState {

    AudioStreamUDP stream;

    public WaitTrying(AudioStreamUDP stream) {
        this.stream = stream;
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("100 TRYING"))
            return new WaitRinging(stream);
        else if(input.startsWith("INVITE")) {
            sendBusy(socket);
        }
        return this;
    }
}
