package examples.grpcclient;

public class ZodiacPerson {
    String month;
    int day;
    String name;
    String sign;

    public ZodiacPerson(String month, int day, String name, String sign) {
        this.month = month;
        this.day = day;
        this.name = name;
        this.sign = sign;
    }
}
