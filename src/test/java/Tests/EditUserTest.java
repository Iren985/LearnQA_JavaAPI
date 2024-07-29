package Tests;

import Lib.ApiCoreRequests;
import Lib.Assertions;
import Lib.BaseTestCase;
import Lib.DataGenerator;
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

@Epic("Editing cases")
@Feature("Edit user")
public class EditUserTest extends BaseTestCase {
    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("This test successfully edit just created user")
    @DisplayName("Test positive edit user")
    public void testEditJustCreatedTest(){
        //generate user

        Map<String,String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        System.out.println(responseCreateAuth.getString("id"));

        //login

        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //edit

        String newName = "Changed Name";
        Map<String,String > editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth,"auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        System.out.println(responseEditUser.asString());


        //get

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth,"auth_sid"))
                .get("https://playground.learnqa.ru/api/user/"+userId)
                .andReturn();

        System.out.println(responseUserData.asString());

        Assertions.assertJsonByName(responseUserData,"firstName", newName);

    }

    @Description("This test checks editing user without auth")
    @DisplayName("Test negative auth user")

    @Test
    public void testEditUserNotAuth(){
        String newName = "Changed Name";
        Map<String,String > editData = new HashMap<>();
        editData.put("firstName", newName);


        Response makePutRequestWithoutLogin = apiCoreRequests
                .makePutRequestWithoutLogin("https://playground.learnqa.ru/api/user/2", editData);


        System.out.println(makePutRequestWithoutLogin.asString());
        System.out.println(makePutRequestWithoutLogin.statusCode());
        Assertions.assertMessageIsCorrect(makePutRequestWithoutLogin,"Auth token not supplied");
        Assertions.assertResponseCodeEquals(makePutRequestWithoutLogin,400);
    }


    @Test
    @Description("This test checks editing other user than auth")
    public void testEditOtherUserAuth(){
            Map<String,String>authData = new HashMap<>();
            authData.put("email","vinkotov@example.com");
            authData.put("password","1234");

            Response responseGetAuth = apiCoreRequests
                    .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

            this.cookie = this.getCookie(responseGetAuth,"auth_sid");
            this.header = this.getHeader(responseGetAuth,"x-csrf-token");
            this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");

        int editedUser = this.userIdOnAuth + 1;

        String newName = "Changed Name";
        Map<String,String > editData = new HashMap<>();
        editData.put("firstName", newName);


        Response makePutRequestWithLogin = apiCoreRequests
                .makePutRequestWithLogin("https://playground.learnqa.ru/api/user/" + editedUser, this.cookie,this.header,editData);


        System.out.println(makePutRequestWithLogin.asString());
        System.out.println(makePutRequestWithLogin.statusCode());
        Assertions.assertMessageIsCorrect(makePutRequestWithLogin,"Please, do not edit test users with ID 1, 2, 3, 4 or 5.");
        Assertions.assertResponseCodeEquals(makePutRequestWithLogin,400);
    }

    @Description("This test checks editing auth user with uncorrect email")
    @Test
    public void testEditUserAuth(){
        Map<String,String>authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");

        String newMail = "vinkotovexample.com";
        Map<String,String > editData = new HashMap<>();
        editData.put("email", newMail);


        Response makePutRequestWithLogin = apiCoreRequests
                .makePutRequestWithLogin("https://playground.learnqa.ru/api/user/" + this.userIdOnAuth , this.cookie,this.header,editData);


        System.out.println(makePutRequestWithLogin.asString());
        System.out.println(makePutRequestWithLogin.statusCode());
        Assertions.assertMessageIsCorrect(makePutRequestWithLogin,"Please, do not edit test users with ID 1, 2, 3, 4 or 5.");
        Assertions.assertResponseCodeEquals(makePutRequestWithLogin,400);
    }

    @Description("This test checks editing auth user with 1 smbl name")
    @Test
    public void testEditUserAuthwith1smblName(){
        Map<String,String>authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");

        String newName = DataGenerator.getRandomShortName();
        Map<String,String > editData = new HashMap<>();
        editData.put("firstName", newName);


        Response makePutRequestWithLogin = apiCoreRequests
                .makePutRequestWithLogin("https://playground.learnqa.ru/api/user/" + this.userIdOnAuth , this.cookie,this.header,editData);


        System.out.println(makePutRequestWithLogin.asString());
        System.out.println(makePutRequestWithLogin.statusCode());
        Assertions.assertMessageIsCorrect(makePutRequestWithLogin,"Please, do not edit test users with ID 1, 2, 3, 4 or 5.");
        Assertions.assertResponseCodeEquals(makePutRequestWithLogin,400);
    }


    //allure serve allure-results/
}
