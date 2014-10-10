import States.Idle;
import States.State;

import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Naknut on 10/10/14.
 */
public class Client {

    static ServerSocket socket;
    static Socket client=null;
    static State state = new Idle();
    static String sipName;

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
                while(true){
                    client=socket.accept();
                    state.handleInput(client);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ConnectionHandler handler = new ConnectionHandler(Integer.parseInt(args[0]));
        sipName = args[1];
        new Thread(handler).start();

        while(client==null){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
}
