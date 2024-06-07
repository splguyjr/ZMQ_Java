package dealer_router;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZPoller;

import java.nio.charset.StandardCharsets;

public class dealer_router_async_client {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java dealer_router_async_client <client_id>");
            System.exit(1);
        }

        String clientId = args[0];
        ClientTask clientTask = new ClientTask(clientId);
        clientTask.start();
    }
}

class ClientTask extends Thread {
    private final String id;

    public ClientTask(String id) {
        this.id = id;
    }

    @Override
    public void run() {
        ZContext context = new ZContext();
        Socket socket = context.createSocket(SocketType.DEALER);
        String identity = this.id;
        socket.setIdentity(id.getBytes(StandardCharsets.US_ASCII));
        socket.connect("tcp://localhost:5570");
        System.out.println("Client " + identity + " started");

        ZPoller poll = new ZPoller(context);
        poll.register(socket, Poller.POLLIN);

        int reqs = 0;
        while (!Thread.currentThread().isInterrupted()) {
            reqs++;
            System.out.println("Req #" + reqs + " sent..");
            socket.send("request #" + reqs);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int sockets = poll.poll(1000);


            if(sockets > 0) {
                String msg = socket.recvStr();
                System.out.println(identity + " received: " + msg);
                }



        }

        socket.close();

    }
}

