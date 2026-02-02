package com.stellarburgers.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellarburgers.models.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiClient {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static Response registerUser(UserRequest user) {
        try {
            return given()
                .header("Content-type", "application/json")
                .body(mapper.writeValueAsString(user))
                .post("/api/auth/register");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации пользователя", e);
        }
    }
    
    public static Response loginUser(LoginRequest loginData) {
        try {
            return given()
                .header("Content-type", "application/json")
                .body(mapper.writeValueAsString(loginData))
                .post("/api/auth/login");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации данных логина", e);
        }
    }
    
    public static Response createOrder(OrderRequest order, String token) {
        try {
            RequestSpecification request = given()
                .header("Content-type", "application/json")
                .body(mapper.writeValueAsString(order));
                
            if (token != null && !token.isEmpty()) {
                request.header("Authorization", token);
            }
            
            return request.post("/api/orders");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации заказа", e);
        }
    }
    
    public static Response getUserOrders(String token) {
        if (token != null && !token.isEmpty()) {
            return given()
                .header("Authorization", token)
                .get("/api/orders");
        } else {
            return given()
                .get("/api/orders");
        }
    }
    
    public static Response deleteUser(String token) {
        return given()
            .header("Authorization", token)
            .delete("/api/auth/user");
    }
    
    public static Response getIngredients() {
        return given()
            .get("/api/ingredients");
    }
}
