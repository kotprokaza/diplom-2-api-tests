package com.stellarburgers.api;

import com.stellarburgers.models.Order;
import com.stellarburgers.models.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static com.stellarburgers.api.Endpoints.*;
import static org.hamcrest.Matchers.*;

public class GetOrdersTests extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    private List<String> ingredientIds;
    private final String PASSWORD = "password123";
    
    @Before
    public void setUpTest() {
        super.setUp();
        testEmail = generateUniqueEmail();
        User user = new User(testEmail, PASSWORD, generateName());
        
        Response response = ApiClient.registerUser(user);
        accessToken = response.path("accessToken");
        
        Response ingredientsResponse = ApiClient.getIngredients();
        ingredientIds = ingredientsResponse.path("data._id");
    }
    
    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            ApiClient.deleteUser(accessToken);
        }
    }
    
    @Test
    @DisplayName("Получение заказов пользователя")
    @Description("Тест на получение заказов конкретного пользователя")
    public void testGetUserOrders() {
        // Сначала создадим заказ
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            Order order = new Order(Arrays.asList(ingredientIds.get(0)));
            ApiClient.createOrder(order, accessToken);
        }
        
        // Получаем заказы пользователя
        Response response = ApiClient.getUserOrders(accessToken);
        
        response.then()
            .statusCode(SC_OK)
            .body("success", equalTo(true))
            .body("orders", notNullValue());
    }
    
    @Test
    @DisplayName("Получение заказов без авторизации")
    @Description("Тест на попытку получить заказы без авторизации")
    public void testGetOrdersWithoutAuth() {
        Response response = ApiClient.getOrdersWithoutAuth();
        
        response.then()
            .statusCode(SC_UNAUTHORIZED)
            .body("success", equalTo(false))
            .body("message", equalTo("You should be authorised"));
    }
}
