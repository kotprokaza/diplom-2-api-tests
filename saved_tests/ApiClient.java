package com.stellarburgers.api;

import com.stellarburgers.models.User;
import com.stellarburgers.models.Order;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiClient {
    
    public static Response registerUser(User user) {
        return given()
            .header("Content-type", "application/json")
            .body(user)
            .when()
            .post(Endpoints.REGISTER);
    }
    
    public static Response loginUser(User credentials) {
        return given()
            .header("Content-type", "application/json")
            .body(credentials)
            .when()
            .post(Endpoints.LOGIN);
    }
    
    public static Response deleteUser(String token) {
        return given()
            .header("Authorization", token)
            .when()
            .delete(Endpoints.USER);
    }
    
    public static Response createOrder(Order order, String token) {
        if (token != null && !token.isEmpty()) {
            return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(order)
                .when()
                .post(Endpoints.ORDERS);
        } else {
            return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(Endpoints.ORDERS);
        }
    }
    
    public static Response getUserOrders(String token) {
        return given()
            .header("Authorization", token)
            .when()
            .get(Endpoints.ORDERS);
    }
    
    public static Response getOrdersWithoutAuth() {
        return given()
            .when()
            .get(Endpoints.ORDERS);
    }
    
    public static Response getIngredients() {
        return given()
            .when()
            .get(Endpoints.INGREDIENTS);
    }
}
