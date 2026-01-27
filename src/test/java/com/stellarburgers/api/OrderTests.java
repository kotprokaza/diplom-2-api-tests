package com.stellarburgers.api;

import com.stellarburgers.models.Order;
import com.stellarburgers.models.User;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class OrderTests extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    private List<String> ingredientIds;
    private final String PASSWORD = "password123";
    
    @Before
    public void setUpTest() {
        super.setUp();
        testEmail = generateUniqueEmail();
        User user = new User(testEmail, PASSWORD, generateName());
        
        // Создаем пользователя
        Response response = ApiClient.registerUser(user);
        accessToken = response.path("accessToken");
        
        // Получаем список доступных ингредиентов
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
    public void testCreateOrderWithAuthAndIngredients() {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            return; // Пропускаем тест, если нет ингредиентов
        }
        
        List<String> ingredients = Arrays.asList(
            ingredientIds.get(0),
            ingredientIds.size() > 1 ? ingredientIds.get(1) : ingredientIds.get(0)
        );
        
        Order order = new Order(ingredients);
        Response response = ApiClient.createOrder(order, accessToken);
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("order", notNullValue())
            .body("order.number", notNullValue());
    }
    
    @Test
    public void testCreateOrderWithoutAuth() {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            return;
        }
        
        List<String> ingredients = Arrays.asList(ingredientIds.get(0));
        Order order = new Order(ingredients);
        
        Response response = ApiClient.createOrder(order, null);
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("order", notNullValue());
    }
    
    @Test
    public void testCreateOrderWithoutIngredients() {
        Order order = new Order(new ArrayList<>());
        Response response = ApiClient.createOrder(order, accessToken);
        
        response.then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("message", equalTo("Ingredient ids must be provided"));
    }
    
    @Test
    public void testCreateOrderWithInvalidIngredientHash() {
        List<String> ingredients = Arrays.asList("invalid_hash_123");
        Order order = new Order(ingredients);
        
        Response response = ApiClient.createOrder(order, accessToken);
        
        response.then()
            .statusCode(500);
    }
}
