import Audio.AudioStreamUDP;
import States.Idle;
import States.State;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * Created by Naknut on 10/10/14.
 */
public class Client {

    static State state = new Idle();
    static String sipName;
    static ServerSocket server;
    static Thread t2;
    static Thread t1;

    private static class InputHandler implements Runnable {

        Socket socket;

        public InputHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = bufferedReader.readLine();
                System.out.println("Received: "+line);
                state.handleInput(line, socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class CallHandler implements Runnable {

        Scanner scanner;

        public void exit() {
            scanner.close();
        }

        @Override
        public void run() {
            System.out.print("Write hostname, port and sip-name of who to call: ");
            scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] tokens = line.split(" ");
            synchronized (state) {
                try {
                    Socket socket = new Socket(tokens[0], Integer.parseInt(tokens[1]));
                    if(state instanceof Idle) {
                        AudioStreamUDP stream = new AudioStreamUDP();
                        ((Idle) state).sendInvite(socket, tokens[2], sipName, stream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("SIP Client started");
        sipName = args[1];
        ServerThread server=new ServerThread(Integer.parseInt(args[0]));
        t1 = new Thread(server);
        t1.start();
        CallHandler callHandler= new CallHandler();
        callHandler.run();
        t2=new Thread(callHandler);
        t1.start();

    }

    private static class ServerThread implements Runnable{
        private int port;
        public ServerThread(int port){
            this.port=port;
        }
        @Override
        public void run() {
            try {
                server = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                Socket socket = null;
                try {
                    socket = server.accept();
                    t1.stop();
                    InputHandler inputHandler = new InputHandler(socket);
                    inputHandler.run();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
