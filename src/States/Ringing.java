package States;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Naknut on 10/10/14.
 */
public class Ringing implements State {

    Boolean okSent = false;

    public Ringing(Socket socket, String sipFrom) {
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
    public State handleInput(Socket socket) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = in.readLine();
            if(okSent && line.equals("ACK"))
                return new Conversation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
