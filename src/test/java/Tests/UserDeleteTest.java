package Tests;

import Lib.*;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


@Epic("Cases for delete user")
@Feature("Delete")
public class UserDeleteTest extends BaseTestCase {
    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String baseUrl = Config.getBaseUrl();

    @Test
    @Description("This test to delete user with ID 2")
    @DisplayName("Test negative check user can't be deleted")
    public void testCheckUserCantBeDeleted(){
        //authorization
        Map<String,String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login", authData);

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");



        //Try to delete itself

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestWithLogin(baseUrl + "user/"+ this.userIdOnAuth, this.cookie,this.header);

        System.out.println(responseDeleteUser.asString());
        System.out.println(responseDeleteUser.statusCode());
        Assertions.assertMessageIsCorrect(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
        Assertions.assertResponseCodeEquals(responseDeleteUser,400);
    }
    @Test
    @Description("This test to delete new created user")
    @DisplayName("Test positive user can be deleted")
    public void testUserCanBeDeleted(){
        //generate user

        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/",userData);



        System.out.println(responseCreateAuth.asString());

        //login

        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login",authData);

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");



        //delete

        Response responseDeleteSuccess = apiCoreRequests
                .makeDeleteRequestWithLogin(baseUrl + "user/"+ this.userIdOnAuth, this.cookie,this.header);


        System.out.println(responseDeleteSuccess.asString());
        System.out.println(responseDeleteSuccess.statusCode());



        //get

        Response responseGetInfo = apiCoreRequests
                .makeGetRequest(baseUrl + "user/"+ this.userIdOnAuth, this.cookie,this.header);

        System.out.println(responseGetInfo.asString());
        System.out.println(responseGetInfo.statusCode());

        Assertions.assertMessageIsCorrect(responseGetInfo, "User not found");
        Assertions.assertResponseCodeEquals(responseGetInfo,404);



    }
    @Test
    @Description("This test to delete other user")
    @DisplayName("Test negative user can't delete other user")
    public void testUserCanDeletedOther(){
        //generate user 1

        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/",userData);

        System.out.println(responseCreateAuth.asString());
        String userIdToDelete = responseCreateAuth.jsonPath().getString("id");

        //generate user 2
        Map<String,String> userData1 = DataGenerator.getRegistrationData();

        Response responseCreateAuth1 = apiCoreRequests
                .makePostRequest(baseUrl + "user/",userData1);

        System.out.println(responseCreateAuth1.asString());

        //login

        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData1.get("email"));
        authData.put("password", userData1.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login",authData);

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");



        //delete

        Response responseDeleteNotSuccess = apiCoreRequests
                .makeDeleteRequestWithLogin(baseUrl + "user/"+ userIdToDelete, this.cookie,this.header);


        System.out.println(responseDeleteNotSuccess.asString());
        System.out.println(responseDeleteNotSuccess.statusCode());


        Assertions.assertMessageIsCorrect(responseDeleteNotSuccess, "This user can only delete their own account.");
        Assertions.assertResponseCodeEquals(responseDeleteNotSuccess,400);



    }



}
