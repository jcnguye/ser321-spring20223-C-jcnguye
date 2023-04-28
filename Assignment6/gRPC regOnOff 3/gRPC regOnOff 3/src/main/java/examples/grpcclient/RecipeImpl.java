package examples.grpcclient;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

public class RecipeImpl extends RecipeGrpc.RecipeImplBase {
    @Override
    public void addRecipe(RecipeReq req, StreamObserver<RecipeResp> responseObserver) {
        JokeRes.Builder response = JokeRes.newBuilder();
    }
    @Override
    public void viewRecipes(Empty empty, StreamObserver<RecipeViewResp> responseObserver) {
        JokeRes.Builder response = JokeRes.newBuilder();
    }
    @Override
    public void rateRecipe(RecipeRateReq req, StreamObserver<RecipeResp> responseObserver) {
        JokeRes.Builder response = JokeRes.newBuilder();
    }
}
