package States;

import Audio.AudioStreamUDP;

import java.net.Socket;


/**
 * Created by Naknut on 10/10/14.
 */
public class WaitTrying extends BusyState {

    AudioStreamUDP stream;
    String sipName;

    public WaitTrying(AudioStreamUDP stream,String sipName) {
        this.sipName=sipName;
        this.stream = stream;
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("100 TRYING"))
            return new WaitRinging(stream,sipName);
        else if(input.startsWith("INVITE")) {
            sendBusy(socket);
        }
        return this;
    }

    @Override
    public State handleUserInput(String input) {
        return this;
    }
}
