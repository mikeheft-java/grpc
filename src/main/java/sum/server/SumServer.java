package sum.server;

import greeting.server.GreetingServerImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class SumServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50052;

        Server server = ServerBuilder
                .forPort(port)
                .addService(new SumServerImpl())
                .build();

        try {
            server.start();
            System.out.println("Server started");
            System.out.println("Listening on port: " + port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Received Shutdown request");
                server.shutdown();
                System.out.println("Server stopped");
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.awaitTermination();
    }
}
