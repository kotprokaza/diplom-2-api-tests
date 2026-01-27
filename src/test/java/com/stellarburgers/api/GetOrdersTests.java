package com.stellarburgers.api;

import com.stellarburgers.models.Order;
import com.stellarburgers.models.User;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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
    public void testGetUserOrders() {
        // Сначала создадим заказ
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            Order order = new Order(Arrays.asList(ingredientIds.get(0)));
            ApiClient.createOrder(order, accessToken);
        }
        
        // Получаем заказы пользователя
        Response response = ApiClient.getUserOrders(accessToken);
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("orders", notNullValue());
    }
    
    @Test
    public void testGetOrdersWithoutAuth() {
        Response response = ApiClient.getOrdersWithoutAuth();
        
        response.then()
            .statusCode(401)
            .body("success", equalTo(false))
            .body("message", equalTo("You should be authorised"));
    }
}
