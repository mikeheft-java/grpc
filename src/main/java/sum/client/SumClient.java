package sum.client;

import com.proto.sum.SumRequest;
import com.proto.sum.SumResponse;
import com.proto.sum.SumServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Objects;

public class SumClient {

    private static void doSum(ManagedChannel channel) {
        System.out.println("Entered doSum()");

        SumServiceGrpc.SumServiceBlockingStub stub = SumServiceGrpc.newBlockingStub(channel);
        SumRequest request = SumRequest.newBuilder().setFirst(3).setSecond(10).build();
        SumResponse response = stub.sum(request);

        System.out.println(request.getFirst() + " + " + request.getSecond() + " = " + response.getResult());
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        if (Objects.equals(args[0], "sum")) {
            doSum(channel);
        } else {
            System.out.println("Keyword Invalid: " + args[0]);
        }
    }
}
