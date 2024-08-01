package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "greet": doGreet(channel); break;
            case "greet_many_times": doGreetManyTimes(channel); break;
            default:
                System.out.println("Keyword Invalid: " + args[0]);
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }

    private static void doGreetManyTimes(ManagedChannel channel) {
        System.out.println("Entered doGreetManyTimes");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);

        stub.greetManyTimes(GreetingRequest.newBuilder().setFirstName("Mike").build()).forEachRemaining(response -> {
            System.out.println(response.getResult());
        });
    }

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Entered doGreet");

        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Mike").build());

        System.out.println("Greeting: " + response.getResult());
    }
}
