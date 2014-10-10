package States;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Naknut on 10/10/14.
 */
public class Ringing extends BusyState {

    Boolean okSent = false;
    int voicePort;

    public Ringing(Socket socket, String sipFrom, int voicePort) {
        this.voicePort = voicePort;
        System.out.println("Call from " + sipFrom);
        System.out.println("Pick up? Y/N");
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("180 RINGING");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.equals("Y")) {
                out.println("200 OK");
                okSent = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(okSent && input.equals("ACK"))
            return new Conversation();
        return this;
    }
}
