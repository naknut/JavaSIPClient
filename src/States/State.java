package States;

import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public interface State {

    public State handleInput(Socket socket);
}
