package com.stellarburgers.api;

import com.stellarburgers.models.OrderRequest;
import com.stellarburgers.models.UserRequest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class UserOrdersTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    
    @Before
    public void setUpTest() {
        super.setUp();
        testEmail = "orders_test_" + System.currentTimeMillis() + "@yandex.ru";
        
        UserRequest user = new UserRequest(testEmail, "password123", "Orders Test User");
        Response response = ApiClient.registerUser(user);
        accessToken = response.path("accessToken");
    }
    
    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            ApiClient.deleteUser(accessToken);
        }
    }
    
    @Test
    @DisplayName("Получение заказов пользователя с авторизацией")
    @Description("Тест на получение заказов конкретного пользователя")
    public void testGetUserOrdersWithAuth() {
        Response ingredientsResponse = ApiClient.getIngredients();
        List<String> ingredientIds = ingredientsResponse.path("data._id");
        
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            OrderRequest order = new OrderRequest(Arrays.asList(ingredientIds.get(0)));
            ApiClient.createOrder(order, accessToken);
        }
        
        Response response = ApiClient.getUserOrders(accessToken);
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("orders", notNullValue());
    }
    
    @Test
    @DisplayName("Получение заказов без авторизации")
    @Description("Тест на попытку получить заказы без авторизации")
    public void testGetOrdersWithoutAuth() {
        Response response = ApiClient.getUserOrders(null);
        
        response.then()
            .statusCode(401)
            .body("success", equalTo(false))
            .body("message", equalTo("You should be authorised"));
    }
}
