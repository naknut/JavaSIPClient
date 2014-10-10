package States;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public class Idle implements State {

    public State sendInvite(Socket socket, String sipTo, String sipFrom, int voicePort) {
        String ipTo = socket.getRemoteSocketAddress().toString();
        String ipFrom = socket.getLocalSocketAddress().toString();
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("INVITE " + sipTo + " " + sipFrom + " " + ipTo + " " + ipFrom + " " + voicePort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new WaitTrying();
    }

    @Override
    public State handleInput(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = in.readLine();
            if(line.startsWith("INVITE")) {
                PrintWriter out;
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("100 TRYING");
                    return new Ringing();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
