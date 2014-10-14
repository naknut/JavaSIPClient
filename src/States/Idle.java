package States;

import Audio.AudioStreamUDP;
import States.Caller.WaitTrying;
import States.Reciver.Ringing;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public class Idle implements State {
    boolean somebodyIsCalling=false;
    String sipName;
    public Idle(String sipName){
        this.sipName=sipName;
    }

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
        return new WaitTrying(stream,sipName);
    }

    @Override
    public State handleInput(String input, Socket socket) {
        if(input.startsWith("INVITE")) {
            String[] strings = input.split(" ");
            String sipTo = strings[1];
            String sipFrom = strings[2];
            String ipTo = strings[3];
            String ipFrom = strings[4];
            int remotePort = Integer.parseInt(strings[5]);
            PrintWriter out;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("100 TRYING");
                return new Ringing(socket, sipFrom, remotePort,sipName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public State handleUserInput(String input) {
        if(input.startsWith("INVITE")){
            String[] tokens = input.split(" ");
            AudioStreamUDP stream = null;
            try {
                Socket socket = new Socket(tokens[1], Integer.parseInt(tokens[2]));
                stream = new AudioStreamUDP();
                sendInvite(socket, tokens[3], sipName, stream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new WaitTrying(stream, sipName);
        }

        System.out.println("Unexpected input");
        return this;
    }

    public State handleInvite(String input, Socket socket){
        if(input.startsWith("INVITE")){
            String[] tokens = input.split(" ");
            AudioStreamUDP stream = null;
            try {
                stream = new AudioStreamUDP();
                sendInvite(socket, tokens[3], sipName, stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new WaitTrying(stream, sipName);
        }

        return this;
    }

}
