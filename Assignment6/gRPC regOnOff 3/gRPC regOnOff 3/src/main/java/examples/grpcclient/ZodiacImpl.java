package examples.grpcclient;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZodiacImpl extends ZodiacGrpc.ZodiacImplBase {
    List<ZodiacPerson> zodiacPersonList = new ArrayList<>();
    Map<String, String> zodiacSigns = new HashMap<>();
    Map<String, String> zodiacTraits = new HashMap<>();
    Map<String, String> zodiacNumber = new HashMap<>();

    HashMap<String, String> zodiacTableDays = new HashMap<>();
    public ZodiacImpl(){
        zodiacPersonList.add(new ZodiacPerson("January",2,"Eddie","Capricorn"));
        zodiacPersonList.add(new ZodiacPerson("February",21,"Brock","Aquarius"));
        zodiacPersonList.add(new ZodiacPerson("March",25,"Phil","Pisces"));
        zodiacPersonList.add(new ZodiacPerson("March",22,"Alex","Pisces"));
        zodiacPersonList.add(new ZodiacPerson("January",14,"Lisa","Capricorn"));
        zodiacPersonList.add(new ZodiacPerson("February",12,"Ben","Aquarius"));
        loadZodiacTable();
        loadZodiacTableTrait();
        loadZodiacTableDays();
        loadZodiacTableNumber();
    }
    public void loadZodiacTable(){
        zodiacSigns.put("January", "Capricorn");
        zodiacSigns.put("February", "Aquarius");
        zodiacSigns.put("March", "Pisces");
        zodiacSigns.put("April", "Aries");
        zodiacSigns.put("May", "Taurus");
        zodiacSigns.put("June", "Gemini");
        zodiacSigns.put("July", "Cancer");
        zodiacSigns.put("August", "Leo");
        zodiacSigns.put("September", "Virgo");
        zodiacSigns.put("October", "Libra");
        zodiacSigns.put("November", "Scorpio");
        zodiacSigns.put("December", "Sagittarius");
    }
    public void loadZodiacTableDays(){
        zodiacTableDays.put("Aries", "March 21 - April 19");
        zodiacTableDays.put("Taurus", "April 20 - May 20");
        zodiacTableDays.put("Gemini", "May 21 - June 20");
        zodiacTableDays.put("Cancer", "June 21 - July 22");
        zodiacTableDays.put("Leo", "July 23 - August 22");
        zodiacTableDays.put("Virgo", "August 23 - September 22");
        zodiacTableDays.put("Libra", "September 23 - October 22");
        zodiacTableDays.put("Scorpio", "October 23 - November 21");
        zodiacTableDays.put("Sagittarius", "November 22 - December 21");
        zodiacTableDays.put("Capricorn", "December 22 - January 19");
        zodiacTableDays.put("Aquarius", "January 20 - February 18");
        zodiacTableDays.put("Pisces", "February 19 - March 20");
    }

    public void loadZodiacTableNumber(){

        zodiacNumber.put("Aries", "first");
        zodiacNumber.put("Taurus", "second");
        zodiacNumber.put("Gemini", "third");
        zodiacNumber.put("Cancer", "fourth");
        zodiacNumber.put("Leo", "fifth");
        zodiacNumber.put("Virgo", "sixth");
        zodiacNumber.put("Libra", "seventh");
        zodiacNumber.put("Scorpio", "eighth");
        zodiacNumber.put("Sagittarius", "ninth");
        zodiacNumber.put("Capricorn", "tenth");
        zodiacNumber.put("Aquarius", "eleventh");
        zodiacNumber.put("Pisces", "twelfth");
    }


    public void loadZodiacTableTrait(){
        zodiacTraits.put("Aries", "assertive and confident.");
        zodiacTraits.put("Taurus", "reliable and patient.");
        zodiacTraits.put("Gemini", "versatile and curious.");
        zodiacTraits.put("Cancer", "nurturing and empathetic.");
        zodiacTraits.put("Leo", "generous and charismatic.");
        zodiacTraits.put("Virgo", "practical and analytical.");
        zodiacTraits.put("Libra", "diplomatic and cooperative.");
        zodiacTraits.put("Scorpio", "passionate and resourceful.");
        zodiacTraits.put("Sagittarius", "adventurous and optimistic.");
        zodiacTraits.put("Capricorn", "responsible and disciplined.");
        zodiacTraits.put("Aquarius", "independent and innovative.");
        zodiacTraits.put("Pisces", "intuitive and artistic.");
    }
    public boolean helperSignCheckList(String sign){
        for(ZodiacPerson person: zodiacPersonList){
            if(person.sign.equals(sign)){
                return true;
            }
        }
        return false;
    }



    public void addZodiacPerson(String month, int day, String name, String sign){
        ZodiacPerson zodiacPerson = new ZodiacPerson(month, day, name, sign);
        zodiacPersonList.add(zodiacPerson);
    }
    public ZodiacPerson findHelper(String sign){
        for(ZodiacPerson person: zodiacPersonList){
            if(person.sign.equals(sign)){
                return person;
            }
        }
        return null;
    }
    @Override
    public void sign(SignRequest req, StreamObserver<SignResponse> responseObserver) {
        SignResponse.Builder response = SignResponse.newBuilder();
        String name = req.getName();
        int day = req.getDay();
        String sign;
        String month = req.getMonth();
        if(zodiacSigns.containsKey(month)){
            sign = zodiacSigns.get(month);
            addZodiacPerson(month,day,name,sign);

            response.setIsSuccess(true);
            response.setMessage(name + " the " + zodiacNumber.get(sign) + " sign in zodiac they are " + sign  + " their days are between " + zodiacTableDays.get(sign) + " traits are: " + zodiacTraits.get(sign));
        }else{
            response.setError("Does not contain right data");
            response.setIsSuccess(false);
            response.setMessage("No valid date");
        }
        SignResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    @Override
    public void find(FindRequest req, StreamObserver<FindResponse> responseObserver) {
        String sign = req.getSign();
        FindResponse.Builder response = FindResponse.newBuilder();
        if(helperSignCheckList(sign)){
            for(ZodiacPerson person:zodiacPersonList){
                if(person.sign.equals(sign)){
                    ZodiacEntry.Builder zodiacEntry = ZodiacEntry.newBuilder();
                    zodiacEntry.setDay(person.day).setName(person.name).setMonth(person.month).setSign(person.sign);
                    response.addEntries(zodiacEntry);
                }
            }
            response.setIsSuccess(true);
        }else{
            response.setIsSuccess(false).setError("Can not finder anyone with zodiac "+ sign);
        }
        FindResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
