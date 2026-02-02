package com.stellarburgers.api;

import com.stellarburgers.models.OrderRequest;
import com.stellarburgers.models.UserRequest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class OrderCreationTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    private List<String> ingredientIds;
    
    @Before
    @Step("Подготовка тестовых данных")
    public void setUpTest() {
        super.setUp();
        testEmail = "order_test_" + System.currentTimeMillis() + "@yandex.ru";
        
        UserRequest user = new UserRequest(testEmail, "password123", "Order Test User");
        Response response = ApiClient.registerUser(user);
        accessToken = response.path("accessToken");
        
        Response ingredientsResponse = ApiClient.getIngredients();
        ingredientIds = ingredientsResponse.path("data._id");
        
        System.out.println("Найдено ингредиентов: " + 
            (ingredientIds != null ? ingredientIds.size() : 0));
    }
    
    @After
    @Step("Очистка тестовых данных")
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            ApiClient.deleteUser(accessToken);
        }
    }
    
    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Тест на успешное создание заказа с авторизацией")
    public void testCreateOrderWithAuthAndIngredients() {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            System.out.println("Нет доступных ингредиентов, пропускаем тест");
            return;
        }
        
        List<String> selectedIngredients = ingredientIds.subList(0, Math.min(2, ingredientIds.size()));
        OrderRequest order = new OrderRequest(selectedIngredients);
        
        Response response = ApiClient.createOrder(order, accessToken);
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("order", notNullValue())
            .body("order.number", notNullValue());
        
        System.out.println("Заказ создан: " + response.path("order.number"));
    }
    
    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Тест на создание заказа без авторизации")
    public void testCreateOrderWithoutAuth() {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            System.out.println("Нет доступных ингредиентов, пропускаем тест");
            return;
        }
        
        String firstIngredient = ingredientIds.get(0);
        OrderRequest order = new OrderRequest(Arrays.asList(firstIngredient));
        
        Response response = ApiClient.createOrder(order, null);
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("order", notNullValue());
        
        System.out.println("Заказ без авторизации создан: " + response.path("order.number"));
    }
    
    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Тест на создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        OrderRequest order = new OrderRequest(new ArrayList<>());
        
        Response response = ApiClient.createOrder(order, accessToken);
        
        response.then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("message", equalTo("Ingredient ids must be provided"));
    }
    
    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Тест на создание заказа с неверным ID ингредиента")
    public void testCreateOrderWithInvalidIngredientHash() {
        OrderRequest order = new OrderRequest(Arrays.asList("invalid_hash_123"));
        
        Response response = ApiClient.createOrder(order, accessToken);
        
        response.then().statusCode(500);
    }
}
