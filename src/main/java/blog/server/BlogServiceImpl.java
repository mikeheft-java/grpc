package blog.server;

import com.google.common.annotations.VisibleForTesting;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {
    @VisibleForTesting
    static final String BLOG_COULDNT_BE_CREATED = "The blog could not be created";
    @VisibleForTesting
    static final String BLOG_COULDNT_BE_DELETED = "The blog could not be deleted";
    @VisibleForTesting
    static final String BLOG_WAS_NOT_FOUND = "The blog with the corresponding id was not found";
    @VisibleForTesting
    static final String ID_CANNOT_BE_EMPTY = "The blog ID cannot be empty";
    private final MongoCollection<Document> mongoCollection;

    private io.grpc.StatusRuntimeException error(Status status, String message, String localizedMessage) {
        return status.withDescription(message).asRuntimeException();
    }
    BlogServiceImpl(MongoClient client) {
        MongoDatabase db = client.getDatabase("blogdb");
        mongoCollection = db.getCollection("blog");
    }

    @Override
    public void createBlog(Blog request, StreamObserver<BlogId> responseObserver) {
        System.out.println("Received Create Blog request");
        Document doc = new Document("author", request.getAuthor())
                .append("title", request.getTitle())
                .append("content", request.getContent());

        System.out.println("Inserting blog...");
        InsertOneResult result;

        try {
            result = mongoCollection.insertOne(doc);
        } catch (MongoException e) {
            responseObserver.onError(error(Status.INTERNAL, BLOG_COULDNT_BE_CREATED, e.getLocalizedMessage()));
            return;
        }

        if (!result.wasAcknowledged() || result.getInsertedId() == null) {
            responseObserver.onError(Status.INTERNAL.withDescription("Blog couldn't be created").asRuntimeException());
            return;
        }

        String id = result.getInsertedId().asObjectId().getValue().toString();
        System.out.println("Inserted blog: " + id);

        responseObserver.onNext(BlogId.newBuilder().setId(id).build());
        responseObserver.onCompleted();
    }
}
