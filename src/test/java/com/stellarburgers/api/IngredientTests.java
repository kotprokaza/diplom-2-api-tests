package com.stellarburgers.api;

import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class IngredientTests extends BaseTest {
    
    @Test
    public void testGetIngredients() {
        Response response = ApiClient.getIngredients();
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data", not(empty()))
            .body("data[0]._id", notNullValue())
            .body("data[0].name", notNullValue())
            .body("data[0].type", notNullValue());
    }
}
