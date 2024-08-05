package blog.client;

import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import javax.annotation.Nullable;

public class BlogClient {

    private BlogClient() {}

    @Nullable
    private static BlogId createBlog(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            Blog blog = Blog.newBuilder()
                    .setAuthor("Mike")
                    .setTitle("New Blog")
                    .setContent("Hello World!")
                    .build();
            BlogId createResponse = stub.createBlog(blog);

            System.out.println("Blog: " + createResponse.getId());
            return createResponse;
        } catch (StatusRuntimeException e) {
            System.out.println("Couldn't create blog");
            e.printStackTrace();
            return null;
        }
    }
    private static void run(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);
        BlogId blogId = createBlog(stub);

        if (blogId == null) {
            return;
        }
    }

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        run(channel);
        System.out.println("Shutting down");
        channel.shutdown();
    }
}
