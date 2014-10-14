package States;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public abstract class BusyState implements State {

    protected void sendBusy(Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("BUSY");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
