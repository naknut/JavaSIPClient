package States;

import java.net.Socket;

/**
 * Created by Naknut on 10/10/14.
 */
public interface State {

    public State handleInput(String input, Socket socket);
    public State handleUserInput(String input);
}
