package Tests;

import Lib.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import Lib.DataGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String baseUrl = Config.getBaseUrl();

    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";


        Map<String,String> userData = new HashMap<>();
        userData.put("email",email);
        userData = DataGenerator.getRegistrationData(userData);
        //userData.put("password","123");
        //userData.put("username","learnqa");
        //userData.put("firstName","learnqa");
        //userData.put("lastName","learnqa");

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(baseUrl + "user/")
                .andReturn();

        System.out.println(responseCreateAuth.asString());
        System.out.println(responseCreateAuth.statusCode());

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");



    }

    @Test
    public void testCreateUserSuccessfully(){
        String email = DataGenerator.getRandomEmail();

        Map<String,String> userData = DataGenerator.getRegistrationData();


        /*Map<String,String> userData = new HashMap<>();
        userData.put("email",email);
        userData.put("password","123");
        userData.put("username","learnqa");
        userData.put("firstName","learnqa");
        userData.put("lastName","learnqa");*/

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(baseUrl + "user/")
                .andReturn();

        System.out.println(responseCreateAuth.asString());
        System.out.println(responseCreateAuth.statusCode());
        System.out.println(userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertResponseHasField(responseCreateAuth,"id");


    }

    @Test
    public void testCreateUserWithUncorrectEmail(){
        String uncorrectedEmail = "vinkotovexample.com";

        Map<String,String> userData = new HashMap<>();
        userData.put("email",uncorrectedEmail);
        userData = DataGenerator.getRegistrationData(userData);

        Response responsePostUserWithUncorrectedEmail = apiCoreRequests
                .makePostRequest(baseUrl + "user", userData);

        System.out.println(responsePostUserWithUncorrectedEmail.asString());
        System.out.println(responsePostUserWithUncorrectedEmail.statusCode());

        Assertions.assertResponseTextEquals(responsePostUserWithUncorrectedEmail,"Invalid email format");
        Assertions.assertResponseCodeEquals(responsePostUserWithUncorrectedEmail,400);

    }

    @ParameterizedTest
    @ValueSource(strings = {"firstName", "lastName", "email", "password", "username"})
    public void postWithNullfield(String value){
        String valueKey = null;

        Map<String,String> userData = new HashMap<>();
        userData.put(value,valueKey);
        userData = DataGenerator.getRegistrationData(userData);

        Response responsePostUserWithUncorrectedEmail = apiCoreRequests
                .makePostRequest(baseUrl + "user", userData);

        System.out.println(responsePostUserWithUncorrectedEmail.asString());
        System.out.println(responsePostUserWithUncorrectedEmail.statusCode());
        System.out.println(userData);

        Assertions.assertResponseTextEquals(responsePostUserWithUncorrectedEmail,"The following required params are missed: " + value);
        Assertions.assertResponseCodeEquals(responsePostUserWithUncorrectedEmail,400);


    }

   @Test
    public void testWith1smblName(){
        String shortName = DataGenerator.getRandomShortName();

       Map<String,String> userData = new HashMap<>();
       userData.put("firstName",shortName);
       userData = DataGenerator.getRegistrationData(userData);

       Response responsePostUserWith1smblName = apiCoreRequests
               .makePostRequest(baseUrl + "user", userData);

       System.out.println(userData);
       System.out.println(responsePostUserWith1smblName.asString());
       System.out.println(responsePostUserWith1smblName.statusCode());

       Assertions.assertResponseTextEquals(responsePostUserWith1smblName,"The value of 'firstName' field is too short");
       Assertions.assertResponseCodeEquals(responsePostUserWith1smblName,400);

   }
    @Test
    public void testWithLongName(){
        String longName = DataGenerator.getRandomLongName();

        Map<String,String> userData = new HashMap<>();
        userData.put("firstName",longName);
        userData = DataGenerator.getRegistrationData(userData);

        Response responsePostUserWithLongName = apiCoreRequests
                .makePostRequest(baseUrl + "user", userData);

        System.out.println(userData);
        System.out.println(responsePostUserWithLongName.asString());
        System.out.println(responsePostUserWithLongName.statusCode());

        Assertions.assertResponseTextEquals(responsePostUserWithLongName,"The value of 'firstName' field is too long");
        Assertions.assertResponseCodeEquals(responsePostUserWithLongName,400);

    }


}
