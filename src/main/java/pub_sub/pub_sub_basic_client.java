package pub_sub;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.SocketType;

import java.util.Random;

public class pub_sub_basic_client {

    public static void main(String[] args)
    {
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(ZMQ.SUB);

        System.out.println("Collecting updates from weather server...");
        socket.connect("tcp://localhost:5556");

        String zip_filter;
        if (args.length>0) {
            zip_filter = args[0]+" ";
        }
        else {
            zip_filter = "10001";
        }
        socket.subscribe(zip_filter);

        int totalTemp = 0;

        for (int update_nbr = 0; update_nbr < 20; update_nbr++) {
            String string = socket.recvStr();
            String[] parts = string.split(" ");
            String zipcode = parts[0];
            int temperature = Integer.parseInt(parts[1]);
            int relHumidity = Integer.parseInt(parts[2]);

            totalTemp += temperature;

            System.out.printf("Receive temperature for zipcode '%s' was %d F\n", zipcode, temperature);
        }

        System.out.printf("Average temperature for zipcode '%s' was %d F\n", zip_filter, totalTemp/20);
    }

}
