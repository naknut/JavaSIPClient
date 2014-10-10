import States.Idle;
import States.State;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Naknut on 10/10/14.
 */
public class Client {

    ServerSocket socket;
    State state = new Idle();
    String sipName;

    private static class ConnectionHandler implements Runnable {

        private int port;

        public ConnectionHandler(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                socket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ConnectionHandler handler = new ConnectionHandler(Integer.parseInt(args[0]));
        sipName = args[1];
        new Thread(handler).start();
    }
}
