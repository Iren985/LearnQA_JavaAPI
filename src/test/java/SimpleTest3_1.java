import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleTest3_1 {
    @Test
    public void SimpleTestPassed(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map")
                .andReturn();
        //assertTrue(response.statusCode() == 200,"Unexpected status code"); //возвращает true/false при fail
        assertEquals(200,response.statusCode(),"Unexpected status code");// выводит код статуса при fail
    }

    @Test
    public void SimpleTestFailed(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map2")
                .andReturn();
        //assertTrue(response.statusCode() == 200,"Unexpected status code"); //возвращает true/false при fail
        assertEquals(200,response.statusCode(),"Unexpected status code");// выводит код статуса при fail
    }
}
