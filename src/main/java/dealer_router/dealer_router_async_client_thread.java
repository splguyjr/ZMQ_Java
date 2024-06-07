package dealer_router;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;

public class dealer_router_async_client_thread {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java dealer_router_async_client_thread <client_id>");
            System.exit(1);
        }

        String clientId = args[0];
        ClientTask1 clientTask = new ClientTask1(clientId);
        clientTask.start();
    }
}

class ClientTask1 extends Thread {
    private final String id;
    private ZContext context;
    private Socket socket;
    private String identity;
    private Poller poll;

    public ClientTask1(String id) {
        this.id = id;
    }

    public void recvHandler() {
        while (true) {
            poll.poll(1000);
            if (poll.pollin(0)) {
                String msg = socket.recvStr();
                System.out.println(identity + " received: " + msg);
            }
        }
    }

    @Override
    public void run() {
        context = new ZContext();
        socket = context.createSocket(SocketType.DEALER);
        identity = String.format("%s", id);
        socket.setIdentity(identity.getBytes(ZMQ.CHARSET));
        socket.connect("tcp://localhost:5570");
        System.out.println("Client " + identity + " started");
        poll = context.createPoller(1);
        poll.register(socket, Poller.POLLIN);
        int reqs = 0;

        Thread clientThread = new Thread(this::recvHandler);
        clientThread.setDaemon(true);
        clientThread.start();

        while (true) {
            reqs++;
            System.out.println("Req #" + reqs + " sent..");
            socket.send("request #" + reqs);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // socket.close(); // useless
        // context.close(); // useless
    }
}
