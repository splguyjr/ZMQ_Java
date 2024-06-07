package pub_sub_and_pull_push;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZPoller;

import java.util.Random;

public class pub_sub_and_pull_push_client {

    public static void main (String[] args){
        ZContext context = new ZContext();
        ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        subscriber.subscribe("".getBytes());
        subscriber.connect("tcp://localhost:5557");
        ZMQ.Socket publisher = context.createSocket(SocketType.PUSH);
        publisher.connect("tcp://localhost:5558");

        ZPoller poll = new ZPoller(context);
        poll.register(subscriber, ZMQ.Poller.POLLIN);
        Random random = new Random(System.currentTimeMillis());

        while(!Thread.currentThread().isInterrupted()){
            if(poll.poll(100) > 0){
                byte[] message = subscriber.recv();
                String s_message = new String(message);
                System.out.println("I: received message " + s_message);
            }else{
                int rand = random.nextInt(100) + 1;
                if(rand < 10){
                    publisher.send(Integer.toString(rand));
                    System.out.println("I: sending message " + rand);
                }
            }
        }
    }

}
