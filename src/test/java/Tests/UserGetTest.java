package Tests;

import Lib.Assertions;
import Lib.BaseTestCase;
import Lib.ApiCoreRequests;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {
    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();


        System.out.println(responseUserData.asString());

        Assertions.assertResponseHasField(responseUserData, "username");
        Assertions.assertJsonHasNoField(responseUserData, "firstName");
        Assertions.assertJsonHasNoField(responseUserData, "lastName");
        Assertions.assertJsonHasNoField(responseUserData, "email");


    }

    @Test
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token",header)
                .cookie("auth_sid",cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();


        String[] expectedFields = {"username","firstName","lastName","email"};
        Assertions.assertJsonHasFields(responseUserData,expectedFields);

        System.out.println(responseUserData.asString());

    }

    //с этого момента и далее все новые запросы выносятся в apiCoreRequests

    @Test
    public void testGetUserDetailsAuthAsOtherUser(){

        Map<String,String>authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);


        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");

        System.out.println(this.cookie);
        System.out.println(this.header);
        System.out.println(this.userIdOnAuth);

        int checkedUser = this.userIdOnAuth + 1;

        System.out.println(checkedUser);



        Response responseGetUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + checkedUser, this.header, this.cookie);



        System.out.println(responseGetUserData.asString());

        Assertions.assertResponseHasField(responseGetUserData, "username");
        Assertions.assertJsonHasNoField(responseGetUserData, "firstName");
        Assertions.assertJsonHasNoField(responseGetUserData, "lastName");
        Assertions.assertJsonHasNoField(responseGetUserData, "email");

    }

}
