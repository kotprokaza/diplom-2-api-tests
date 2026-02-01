package com.stellarburgers.api;

import com.stellarburgers.helpers.JsonHelper;
import com.stellarburgers.models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserRegistrationTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    
    @Before
    public void setUpTest() {
        super.setUp();
        testEmail = "test_" + System.currentTimeMillis() + "@yandex.ru";
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
    public void testCreateUniqueUser() {
        User user = new User(testEmail, "password123", "Test User");
        String jsonBody = JsonHelper.toJson(user);
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/register");
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("user.email", equalTo(testEmail.toLowerCase()))
            .body("user.name", equalTo("Test User"));
        
        accessToken = response.path("accessToken");
    }
    
    @Test
    public void testCreateExistingUser() {
        User user = new User(testEmail, "password123", "Test User");
        String jsonBody = JsonHelper.toJson(user);
        
        // Первая регистрация
        Response firstResponse = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/register");
        
        accessToken = firstResponse.path("accessToken");
        
        // Вторая попытка регистрации
        Response secondResponse = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/register");
        
        secondResponse.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", equalTo("User already exists"));
    }
    
    @Test
    public void testCreateUserWithoutEmail() {
        // Тест 1: без email
        String jsonBody = "{\"password\": \"password123\", \"name\": \"Test User\"}";
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/register");
        
        response.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", equalTo("Email, password and name are required fields"));
    }
    
    @Test
    public void testCreateUserWithoutPassword() {
        // Тест 2: без пароля
        String jsonBody = "{\"email\": \"" + testEmail + "\", \"name\": \"Test User\"}";
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/register");
        
        response.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", equalTo("Email, password and name are required fields"));
    }
    
    @Test
    public void testCreateUserWithoutName() {
        // Тест 3: без имени
        String jsonBody = "{\"email\": \"" + testEmail + "\", \"password\": \"password123\"}";
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
            .when()
            .post("/api/auth/register");
        
        response.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", equalTo("Email, password and name are required fields"));
    }
}
