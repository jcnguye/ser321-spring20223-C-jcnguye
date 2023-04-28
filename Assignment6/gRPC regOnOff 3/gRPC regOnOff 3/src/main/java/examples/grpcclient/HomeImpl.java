package examples.grpcclient;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;



public class HomeImpl extends HometownsGrpc.HometownsImplBase{
    List<People> peopleArrayList = new ArrayList<>();
    public HomeImpl(){
        peopleArrayList.add(new People("West","Phoenix","Dave"));
        peopleArrayList.add(new People("Midwest","Ohio","Gary"));
        peopleArrayList.add(new People("South","Tampa","Brock"));
    }
    public People findPeople (String city){
        for(People people: peopleArrayList){
            if(Objects.equals(people.city, city)){
                return people;
            }
        }
        return null;
    }
    public void addPeople(String name, String reign,String city){
        System.out.println("Adding name: " + name);
        System.out.println("Adding region: " + reign);
        System.out.println("Adding city: " + city);
        People people = new People(reign,city,name);
        peopleArrayList.add(people);
    }
    @Override
    public void search(HometownsSearchRequest req, StreamObserver<HometownsReadResponse> responseObserver) {
        String city = req.getCity();
        HometownsReadResponse.Builder response = HometownsReadResponse.newBuilder();
        System.out.println(city);
        System.out.println("Received from client: " + city);
        People people = findPeople(city);
        if(people == null){
            System.out.println("Does not contain that person\n");
            response.setIsSuccess(false);
            response.setError("Does not contain that persons city");
        }else{
            System.out.println("Contains that person\n");
            response.setIsSuccess(true);
            Hometown.Builder hometown = Hometown.newBuilder();
            hometown.setCity(people.city);
            hometown.setName(people.name);
            hometown.setRegion(people.region);
            response.addHometowns(hometown);
        }
        HometownsReadResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();

    }
    @Override
    public void read(Empty empty, StreamObserver<HometownsReadResponse> responseObserver) {
//        System.out.println("Received from client: " + req.getCity());
//        JokeRes.Builder response = JokeRes.newBuilder();
        HometownsReadResponse.Builder response = HometownsReadResponse.newBuilder();
        response.setIsSuccess(true);
//        response.addAllHometowns(peopleArrayList);
        for(People people:peopleArrayList){
            Hometown.Builder hometown = Hometown.newBuilder();
            hometown.setCity(people.city);
            hometown.setName(people.name);
            hometown.setRegion(people.region);
            response.addHometowns(hometown);
        }
        HometownsReadResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();

    }
    @Override
    public void write (HometownsWriteRequest writeRequest, StreamObserver<HometownsWriteResponse> responseObserver) {
//        System.out.println("Received from client: " + req.getCity());
        addPeople(writeRequest.getHometown().getName(),writeRequest.getHometown().getRegion(),writeRequest.getHometown().getCity());
        HometownsWriteResponse.Builder response = HometownsWriteResponse.newBuilder();
        response.setIsSuccess(true);

        HometownsWriteResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();

    }
}
