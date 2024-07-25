package Lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertJsonByName(Response Response, String name, int expectedValue){
        Response.then().assertThat().body("$", hasKey(name));

        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "Json value is not equal to expected value");
    }

    public static void assertJsonByName(Response Response, String name, String expectedValue){
        Response.then().assertThat().body("$", hasKey(name));

        String value = Response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "Json value is not equal to expected value");
    }

   public static void assertResponseTextEquals(Response Response,String expectedAnswer) {
      assertEquals(
              expectedAnswer,
              Response.asString(),
              "Response text is not as expected"
      );
   }
    public static void assertResponseCodeEquals(Response Response,Integer expectedStatusCode) {
        assertEquals(
                expectedStatusCode,
                Response.statusCode(),
                "Response statusCode is not as expected"
        );
    }

    public static void assertResponseHasField(Response Response, String expectedField) {
        Response.then().assertThat().body("$", hasKey(expectedField));

    }

    public static void assertJsonHasFields(Response Response, String[] expectedFieldNames){
        for (String expectedFieldName : expectedFieldNames){
            Assertions.assertResponseHasField(Response,expectedFieldName);
        }
    }

    public static void assertJsonHasNoField(Response Response, String unexpectedField) {
        Response.then().assertThat().body("$", not(hasKey(unexpectedField)));

    }



}
