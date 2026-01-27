package com.stellarburgers.api;

import com.stellarburgers.models.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import static com.stellarburgers.api.Endpoints.*;
import static org.hamcrest.Matchers.*;

public class UserRegistrationTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    private final String PASSWORD = "password123";
    
    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            ApiClient.deleteUser(accessToken);
        }
    }
    
    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Тест на успешную регистрацию нового пользователя")
    public void testCreateUniqueUser() {
        testEmail = generateUniqueEmail();
        User user = new User(testEmail, PASSWORD, generateName());
        
        Response response = ApiClient.registerUser(user);
        
        response.then()
            .statusCode(SC_OK)
            .body("success", equalTo(true))
            .body("user.email", equalTo(testEmail.toLowerCase()))
            .body("user.name", equalTo(user.getName()));
            
        accessToken = response.path("accessToken");
    }
    
    @Test
    @DisplayName("Создание существующего пользователя")
    @Description("Тест на попытку регистрации уже существующего пользователя")
    public void testCreateExistingUser() {
        testEmail = generateUniqueEmail();
        User user = new User(testEmail, PASSWORD, generateName());
        
        // Сначала создаем пользователя
        Response firstResponse = ApiClient.registerUser(user);
        accessToken = firstResponse.path("accessToken");
        
        // Пытаемся создать того же пользователя снова
        Response secondResponse = ApiClient.registerUser(user);
        
        secondResponse.then()
            .statusCode(SC_FORBIDDEN)
            .body("success", equalTo(false))
            .body("message", equalTo("User already exists"));
    }
    
    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Тест на регистрацию без заполнения email")
    public void testCreateUserWithoutEmail() {
        User user = new User(null, PASSWORD, generateName());
        
        Response response = ApiClient.registerUser(user);
        
        response.then()
            .statusCode(SC_FORBIDDEN)
            .body("success", equalTo(false))
            .body("message", equalTo("Email, password and name are required fields"));
    }
    
    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Тест на регистрацию без заполнения пароля")
    public void testCreateUserWithoutPassword() {
        testEmail = generateUniqueEmail();
        User user = new User(testEmail, null, generateName());
        
        Response response = ApiClient.registerUser(user);
        
        response.then()
            .statusCode(SC_FORBIDDEN)
            .body("success", equalTo(false))
            .body("message", equalTo("Email, password and name are required fields"));
    }
    
    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Тест на регистрацию без заполнения имени")
    public void testCreateUserWithoutName() {
        testEmail = generateUniqueEmail();
        User user = new User(testEmail, PASSWORD, null);
        
        Response response = ApiClient.registerUser(user);
        
        response.then()
            .statusCode(SC_FORBIDDEN)
            .body("success", equalTo(false))
            .body("message", equalTo("Email, password and name are required fields"));
    }
}
