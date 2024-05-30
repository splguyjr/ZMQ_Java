package req_rep;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class req_rep_basic_client
{
    public static void main(String[] args)
    {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://localhost:5555");

            for (int request=0; request<10; request++) {
                System.out.println("Sending request " + request + " ...");
                socket.send("Hello".getBytes(ZMQ.CHARSET), 0);
                byte[] message = socket.recv();
                System.out.println("Received reply " + request + "[ " + new String(message, ZMQ.CHARSET) + " ]");
            }
        }
    }
}