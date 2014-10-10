package Audio;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Naknut on 10/10/14.
 */
public class AudioStreamUDP {

    public static final int BUFFER_VS_FRAMES_RATIO = 16; //32
    public static final boolean DEBUG = false;
    public static final int TIME_OUT = 5000; // Time out for receiving packets

    public AudioStreamUDP() throws IOException {
        this.receiverSocket = new DatagramSocket();
        //receiverSocket.setSoTimeout(TIME_OUT);
        this.senderSocket = new DatagramSocket();

        format = new AudioFormat(22050, 16, 1, true, true); // 44100
        this.receiver = new Receiver(receiverSocket, format);
        this.sender = new Sender(senderSocket, format);
    }

    public int getLocalPort() {
        return receiverSocket.getLocalPort();
    }

    public synchronized void connectTo(InetAddress remoteAddress, int remotePort)
            throws IOException {
        sender.connectTo(remoteAddress, remotePort);
        receiver.connectTo(remoteAddress);
    }

    public synchronized void startStreaming() {
        receiver.startActivity();
        sender.startActivity();
    }

    public synchronized void stopStreaming() {
        receiver.stopActivity();
        sender.stopActivity();
    }

    public synchronized void close()  {
        if(receiverSocket != null) receiverSocket.close();
        if(senderSocket != null) senderSocket.close();
    }

    private DatagramSocket senderSocket, receiverSocket;
    private Receiver receiver = null;
    private Sender sender = null;
    private AudioFormat format;
}
