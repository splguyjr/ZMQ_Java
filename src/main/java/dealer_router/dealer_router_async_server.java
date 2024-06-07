package dealer_router;

import org.zeromq.*;

import java.util.ArrayList;
import java.util.List;

public class dealer_router_async_server {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java dealer_router_async_server <number_of_servers>");
            System.exit(1);
        }

        int numServers = Integer.parseInt(args[0]);

        ServerTask serverTask = new ServerTask(numServers);
        serverTask.start();
        try {
            serverTask.join();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
}

class ServerTask extends Thread {
    private final int numServers;

    public ServerTask(int numServers) {
        this.numServers = numServers;
    }

    @Override
    public void run() {
            ZContext context = new ZContext();
            ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
            frontend.bind("tcp://*:5570");

            ZMQ.Socket backend = context.createSocket(SocketType.DEALER);
            backend.bind("inproc://backend");

            List<ServerWorker> workers = new ArrayList<>();

            for (int i = 0; i < numServers; i++) {
                ServerWorker worker = new ServerWorker(context, i);
                worker.start();
                workers.add(worker);
            }

            ZMQ.proxy(frontend, backend, null);

            frontend.close();
            backend.close();
            context.destroy();
    }
}


class ServerWorker extends Thread {
    private final ZContext context;
    private final int id;

    public ServerWorker(ZContext context, int id) {
        this.context = context;
        this.id = id;
    }

    @Override
    public void run() {
        ZMQ.Socket worker = context.createSocket(SocketType.DEALER);
        worker.connect("inproc://backend");
        System.out.println("Worker#" + id + " started");

        while (!Thread.currentThread().isInterrupted()) {
            ZMsg recvMsg = ZMsg.recvMsg(worker);

            ZFrame frame1 = recvMsg.pop();
            ZFrame frame2 = recvMsg.pop();
            recvMsg.destroy();//메모리에서 객체 해제

            String ident = new String(frame1.getData());
            String msg = new String(frame2.getData());

            System.out.println("Worker#" + id + " received " + msg + " from " + ident);

            frame1.send(worker, ZFrame.REUSE + ZFrame.MORE);
            frame2.send(worker, ZFrame.REUSE);
        }

//        worker.close();
    }
}