package States;

import Audio.AudioStreamUDP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public class Idle implements State {

    public State sendInvite(Socket socket, String sipTo, String sipFrom, AudioStreamUDP stream) {
        String ipTo = socket.getRemoteSocketAddress().toString();
        String ipFrom = socket.getLocalSocketAddress().toString();
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("INVITE " + sipTo + " " + sipFrom + " " + ipTo + " " + ipFrom + " " + stream.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new WaitTrying(stream);
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("INVITE")) {
            String[] strings = input.split(" ");
            String sipTo = strings[1];
            String sipFrom = strings[2];
            String ipTo = strings[3];
            String ipFrom = strings[4];
            int voicePort = Integer.parseInt(strings[5]);
            PrintWriter out;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("100 TRYING");
                return new Ringing(socket, sipFrom, voicePort);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
