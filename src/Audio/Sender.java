package Audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class Sender implements Runnable {

    Sender(DatagramSocket socket, AudioFormat format) {
        this.socket = socket;
        this.format = format;
    }

    public void connectTo(InetAddress remoteAddress, int remotePort) throws IOException {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        //socket.connect(new InetSocketAddress(remoteAddress, remotePort));
    }

    synchronized  void startActivity() {
        if(senderThread == null) {
            senderThread = new Thread(this);
            senderThread.start();
        }
    }

    synchronized void stopActivity() {
        senderThread = null;
    }

    public void run() {
        // Make the run method a private matter
        if(senderThread != Thread.currentThread()) return;

        try {
            initializeLine();

            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / AudioStreamUDP.BUFFER_VS_FRAMES_RATIO;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead;
            DatagramPacket packet = null;

            line.start();
            int packets = 0;
           // System.out.println("Ready");
            while (senderThread != null) {
                if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                    break;
                }
                packet = new DatagramPacket(data, numBytesRead, remoteAddress, remotePort);
                socket.send(packet);
                if(AudioStreamUDP.DEBUG) {
                    System.out.println("Bytes sent = " + numBytesRead + ", packets = " + packets++);
                }
            }
        }
        catch(SocketException se) {
            System.out.println("Sender socket is closed");
            // Exception is thrown if socket is closed before last call to send.
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            this.cleanUp();
        }
    }

    private DatagramSocket socket = null;
    private InetAddress remoteAddress = null;
    private int remotePort = 0;
    private Thread senderThread = null;
    private TargetDataLine line = null;
    private AudioFormat format = null;

    private void initializeLine() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line matching " + info + " not supported.");
            return;
        }

        //line = (TargetDataLine) AudioSystem.getLine(info);
        line = getTargetDataLine(format);
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
    private TargetDataLine getTargetDataLine(AudioFormat format) {
        Exception audioException = null;
        try {
            DataLine.Info info =
                    new DataLine.Info(TargetDataLine.class, format);

            for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
                TargetDataLine dataline = null;
                try {
                    Mixer mixer = AudioSystem.getMixer(mi);
                    dataline = (TargetDataLine) mixer.getLine(info);
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
            throw new IllegalStateException("Couldn't aquire a dataline, " +
                    "this computer doesn't seem to have audio output?");
        } else {
            throw new IllegalStateException("Couldn't aquire a dataline, probably " +
                    "because all are in use. Last exception:", audioException);
        }
    }
}