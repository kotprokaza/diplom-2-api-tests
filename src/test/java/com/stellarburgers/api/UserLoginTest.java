package com.stellarburgers.api;

import com.stellarburgers.helpers.JsonHelper;
import com.stellarburgers.models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserLoginTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    private String password = "password123";
    
    @Before
    public void setUpTest() {
        super.setUp();
        testEmail = "login_test_" + System.currentTimeMillis() + "@yandex.ru";
        
        User user = new User(testEmail, password, "Login Test User");
        String jsonBody = JsonHelper.toJson(user);
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/register");
        
        // Извлекаем токен безопасно
        String responseBody = response.getBody().asString();
        if (response.getStatusCode() == 200) {
            accessToken = response.jsonPath().getString("accessToken");
        }
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
    public void testLoginWithExistingUser() {
        User loginData = new User(testEmail, password, null);
        String jsonBody = JsonHelper.toJson(loginData);
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/login");
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("user.email", equalTo(testEmail.toLowerCase()))
            .body("user.name", equalTo("Login Test User"));
    }
    
    @Test
    public void testLoginWithWrongPassword() {
        User loginData = new User(testEmail, "wrongpassword", null);
        String jsonBody = JsonHelper.toJson(loginData);
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/login");
        
        response.then()
            .statusCode(401)
            .body("success", equalTo(false))
            .body("message", equalTo("email or password are incorrect"));
    }
    
    @Test
    public void testLoginWithWrongEmail() {
        User loginData = new User("wrong_" + testEmail, password, null);
        String jsonBody = JsonHelper.toJson(loginData);
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/login");
        
        response.then()
            .statusCode(401)
            .body("success", equalTo(false))
            .body("message", equalTo("email or password are incorrect"));
    }
}
