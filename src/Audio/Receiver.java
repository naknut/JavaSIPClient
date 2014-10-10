package Audio;

import javax.sound.sampled.*;
import java.net.*;

/**
 * Created by Naknut on 10/10/14.
 */
public class Receiver implements Runnable {

    Receiver(DatagramSocket socket, AudioFormat format) {
    this.socket = socket;
    this.format = format;
}

    void connectTo(InetAddress remoteHost) {
        this.remoteHost = remoteHost;
    }

    synchronized  void startActivity() {
        if(receiverThread == null) {
            receiverThread = new Thread(this);
            receiverThread.start();
        }
    }

    synchronized void stopActivity() {
        receiverThread = null;
    }

    public void run() {
        // Make the run method a private matter
        if(receiverThread != Thread.currentThread()) return;

        try {
            initializeLine();

            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / AudioStreamUDP.BUFFER_VS_FRAMES_RATIO;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            if(AudioStreamUDP.DEBUG) {
                System.out.println("bufferLengthInFrames = " + bufferLengthInFrames);
                System.out.println("bufferLengthInBytes = " + bufferLengthInBytes);
            }
            byte[] data = new byte[bufferLengthInBytes];
            DatagramPacket packet = new DatagramPacket(data, bufferLengthInBytes);
            int numBytesRead = 0;

            line.start();
            int packets = 0;
            while (receiverThread != null) {
                socket.receive(packet);
                // Who's the sender?
                if(remoteHost.equals(packet.getAddress())) {
                    numBytesRead = packet.getLength();
                    if(AudioStreamUDP.DEBUG) {
                        System.out.println("Received bytes = " + numBytesRead + ", packets = " + packets++);
                    }
                    int numBytesRemaining = numBytesRead;
                    while (numBytesRemaining > 0 ) {
                        numBytesRemaining -= line.write(data, 0, numBytesRemaining);
                    }
                }
            }
        }
        catch(SocketTimeoutException ste) {
            System.out.println("Receive call timed out");
        }
        catch(SocketException se) {
            System.out.println("Receiver socket is closed");
            // If the thread is blocked in a receive call, an exception is thrown when
            // the socket is closed, causing the thread to unblock.
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            this.cleanUp();
        }
    }

    private DatagramSocket socket = null;
    private InetAddress remoteHost;
    private Thread receiverThread = null;
    private SourceDataLine line = null;
    private AudioFormat format = null;

    private void initializeLine() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line matching " + info + " not supported.");
            return;
        }

        //line = (SourceDataLine) AudioSystem.getLine(info);
        line = getSourceDataLine(format);
        if(!line.isOpen()) {
            line.open(format, line.getBufferSize());
        }
    }

    private void cleanUp() {
        try {
            if(line != null) {
                line.stop();
                line.close();
            }
        } catch(Exception e) {}
    }

    protected void finalize() {
        this.cleanUp();
    }

    /**
     * Thanks to: Paulo Levi.
     * Lines can fail to open because they are already in use.
     * Java sound uses OSS and some linuxes are using pulseaudio.
     * OSS needs exclusive access to the line, and pulse audio
     * highjacks it. Try to open another line.
     * @param format
     * @return a open line
     * @throws IllegalStateException if it can't open a dataline for the
     * audioformat.
     */
    private SourceDataLine getSourceDataLine(AudioFormat format) {
        Exception audioException = null;
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
                SourceDataLine dataline = null;
                try {
                    Mixer mixer = AudioSystem.getMixer(mi);
                    dataline = (SourceDataLine) mixer.getLine(info);
                    dataline.open(format);
                    dataline.start();
                    return dataline;
                } catch (Exception e) {
                    audioException = e;
                }
                if (dataline != null) {
                    try {
                        dataline.close();
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error trying to aquire dataline.", e);
        }
        if (audioException == null) {
            throw new IllegalStateException("Couldn't aquire a dataline, this " +
                    "computer doesn't seem to have audio output?");
        } else {
            throw new IllegalStateException("Couldn't aquire a dataline, probably " +
                    "because all are in use. Last exception:", audioException);
        }
    }
}
