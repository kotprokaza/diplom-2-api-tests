package com.stellarburgers.api;

import com.stellarburgers.helpers.JsonHelper;
import com.stellarburgers.models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    private List<String> ingredientIds;
    
    @Before
    public void setUpTest() {
        super.setUp();
        testEmail = "order_test_" + System.currentTimeMillis() + "@yandex.ru";
        
        // Создаем пользователя
        User user = new User(testEmail, "password123", "Order Test User");
        String jsonBody = JsonHelper.toJson(user);
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/register");
        
        // Извлекаем токен безопасно
        if (response.getStatusCode() == 200) {
            accessToken = response.jsonPath().getString("accessToken");
        }
        
        // Получаем список ингредиентов
        Response ingredientsResponse = given()
            .when()
            .get("/api/ingredients");
        
        ingredientIds = ingredientsResponse.jsonPath().getList("data._id");
    }
    
    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            given()
                .header("Authorization", accessToken)
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(202);
        }
    }
    
    @Test
    public void testCreateOrderWithAuthAndIngredients() {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            System.out.println("Нет доступных ингредиентов, пропускаем тест");
            return;
        }
        
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("ingredients", ingredientIds.subList(0, Math.min(2, ingredientIds.size())));
        String jsonBody = JsonHelper.toJson(orderData);
        
        Response response = given()
            .header("Content-type", "application/json")
            .header("Authorization", accessToken)
            .body(jsonBody)
            .when()
            .post("/api/orders");
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("order", notNullValue())
            .body("order.number", notNullValue());
    }
    
    @Test
    public void testCreateOrderWithoutAuth() {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            System.out.println("Нет доступных ингредиентов, пропускаем тест");
            return;
        }
        
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("ingredients", List.of(ingredientIds.get(0)));
        String jsonBody = JsonHelper.toJson(orderData);
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/orders");
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("order", notNullValue());
    }
    
    @Test
    public void testCreateOrderWithoutIngredients() {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("ingredients", List.of());
        String jsonBody = JsonHelper.toJson(orderData);
        
        Response response = given()
            .header("Content-type", "application/json")
            .header("Authorization", accessToken)
            .body(jsonBody)
            .when()
            .post("/api/orders");
        
        response.then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("message", equalTo("Ingredient ids must be provided"));
    }
    
    @Test
    public void testCreateOrderWithInvalidIngredientHash() {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("ingredients", List.of("invalid_hash_123"));
        String jsonBody = JsonHelper.toJson(orderData);
        
        Response response = given()
            .header("Content-type", "application/json")
            .header("Authorization", accessToken)
            .body(jsonBody)
            .when()
            .post("/api/orders");
        
        response.then()
            .statusCode(500);
    }
    
    @Test
    public void testGetUserOrders() {
        // Сначала создаем заказ
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("ingredients", List.of(ingredientIds.get(0)));
            String jsonBody = JsonHelper.toJson(orderData);
            
            given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(jsonBody)
                .when()
                .post("/api/orders");
        }
        
        Response response = given()
            .header("Authorization", accessToken)
            .when()
            .get("/api/orders");
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("orders", notNullValue());
    }
    
    @Test
    public void testGetOrdersWithoutAuth() {
        Response response = given()
            .when()
            .get("/api/orders");
        
        response.then()
            .statusCode(401)
            .body("success", equalTo(false))
            .body("message", containsString("authorised"));
    }
}
