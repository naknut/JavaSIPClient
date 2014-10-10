package States;

import java.net.Socket;


/**
 * Created by Naknut on 10/10/14.
 */
public class WaitTrying implements State {


    @Override
    public State handleInput(Socket socket) {

        return this;
    }
}
