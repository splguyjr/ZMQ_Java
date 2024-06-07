package pub_sub_and_pull_push;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;


public class pub_sub_and_pull_push_server {

    public static void main(String[] args)
    {
        ZContext context = new ZContext();
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://*:5557");
        ZMQ.Socket collector = context.createSocket(SocketType.PULL);
        collector.bind("tcp://*:5558");

        while (!Thread.currentThread().isInterrupted()) {
            byte[] message = collector.recv(0);
            System.out.println("I: publishing update " + new String(message, ZMQ.CHARSET));
            publisher.send(message);
        }

    }

}
