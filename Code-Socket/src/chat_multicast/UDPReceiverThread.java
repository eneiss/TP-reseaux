package chat_multicast;

import java.io.*;
import java.net.*;
import chat_TCP.ChatClient;

public class UDPReceiverThread extends Thread {

    private MulticastSocket socket;
    private byte[] buffer;
    private static final int bufferSize = 1000;

    UDPReceiverThread(MulticastSocket socket) {
        this.socket = socket;
    }

    public void run(){

        buffer = new byte[bufferSize];

        try {
            while (true) {

                // Build a datagram packet for response

                DatagramPacket recv = new DatagramPacket(buffer,
                                            buffer.length);
                // Receive a datagram packet response
                socket.receive(recv);

                UDPClient.printMessage(new String(recv.getData()));

                clearBuffer();

            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    private void clearBuffer(){
        for (int i = 0; i < bufferSize; ++i){
            buffer[i] = 0;
        }
    }
}
