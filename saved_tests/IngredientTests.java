package com.stellarburgers.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static com.stellarburgers.api.Endpoints.*;
import static org.hamcrest.Matchers.*;

public class IngredientTests extends BaseTest {
    
    @Test
    @DisplayName("Получение списка ингредиентов")
    @Description("Тест на получение доступных ингредиентов")
    public void testGetIngredients() {
        Response response = ApiClient.getIngredients();
        
        response.then()
            .statusCode(SC_OK)
            .body("success", equalTo(true))
            .body("data", not(empty()))
            .body("data[0]._id", notNullValue())
            .body("data[0].name", notNullValue())
            .body("data[0].type", notNullValue());
    }
}
