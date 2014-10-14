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

    static State state;
    static String sipName;
    static ServerSocket server;
    static Thread userInputThread;
    static Thread serverThread;

    private static class SocketInputHandler implements Runnable {

        Socket socket;

        public SocketInputHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while(!socket.isClosed()){
                    line = bufferedReader.readLine();
                    System.out.println("Received: "+line);
                    if(line != null)
                    synchronized (state) {
                        state = state.handleInput(line, socket);
                    } else {
                        System.out.println("Remote closed its socket");
                        socket.close();
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection closed");
            }
        }
    }

    private static class UserInputHandler implements Runnable {

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if(input.startsWith("/exit")){
                    System.exit(0);
                }
                else if(input.startsWith("INVITE")){
                    synchronized (state){
                        if(state instanceof Idle){
                            String[] tokens = input.split(" ");
                            AudioStreamUDP stream = null;
                            try {
                                Socket socket = new Socket(tokens[1], Integer.parseInt(tokens[2]));
                                SocketInputHandler inputHandler=new SocketInputHandler(socket);
                                new Thread(inputHandler).start();
                                state = ((Idle) state).handleInvite(input, socket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else if(input.startsWith("/state")){
                    System.out.println("Current state: "+state.getClass().getSimpleName());
                }
                else{
                    synchronized (state) {
                        state = state.handleUserInput(input);
                    }
                }
            }
        }
    }




    public static void main(String[] args) {
        System.out.println("SIP Client started");
        sipName = args[1];
        state=new Idle(sipName);
        serverThread = new Thread(new ServerThread(Integer.parseInt(args[0])));
        serverThread.start();
        userInputThread=new Thread(new UserInputHandler());
        userInputThread.start();

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
                    SocketInputHandler inputHandler = new SocketInputHandler(socket);
                    new Thread(inputHandler).start();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void resetState(){
       state=new Idle(sipName);
    }
}
