package TestData;

import Pojo.User;
import com.github.javafaker.Faker;

public class Data {
    public static String apiKey = "reqres-free-v1\n";
    public static String BaseUri = "https://reqres.in/api/";
    static Faker faker = new Faker();
    public static String fakeEmail = faker.internet().emailAddress();
    public static String fakeUserName = faker.name().username();
    public static String fakePassword = faker.internet().password();
    User registerBody = new User();
}
