package pub_sub;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.util.Random;

public class pub_sub_basic_server {

    public static void main(String[] args)
    {
        System.out.println("Publishing updates at weather server...");
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(ZMQ.PUB);
        socket.bind("tcp://*:5556");

        Random random = new Random();

        while (!Thread.currentThread().isInterrupted()) {
            int zipcode = random.nextInt(99999) + 1; // Range: 1 to 99999
            int temperature = random.nextInt(216) - 80; // Range: -80 to 135
            int relHumidity = random.nextInt(51) + 10; // Range: 10 to 60

            String message = String.format("%d %d %d", zipcode, temperature, relHumidity);
            socket.send(message.getBytes(ZMQ.CHARSET));
        }

    }

}
