package chat_multicast;

import java.io.*;
import java.net.*;
import chat_TCP.ChatClient;

public class UDPReceiverThread extends Thread {

    private MulticastSocket socket;

    UDPReceiverThread(MulticastSocket socket) {
        this.socket = socket;
    }

    public void run(){
        byte[] buffer = new byte[1000];

        try {
            while (true) {

                // Build a datagram packet for response

                DatagramPacket recv = new DatagramPacket(buffer,
                                            buffer.length);
                // Receive a datagram packet response
                socket.receive(recv);

                UDPClient.printMessage(new String(recv.getData()));

            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }
}
