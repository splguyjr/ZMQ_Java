package req_rep;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

//reference -> https://zeromq.org/languages/java/

public class req_rep_basic_server
{
    public static void main(String[] args)
    {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] message = socket.recv(0);

                // Print the message
                System.out.println(
                        "Received request: " + new String(message, ZMQ.CHARSET)
                );
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Send a response
                String response = "World!";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        }
    }
}