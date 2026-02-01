package com.stellarburgers.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

@DisplayName("Проверка работы Allure отчетов")
public class AllureSimpleTest {
    
    @Test
    @DisplayName("Простейший тест для Allure")
    @Description("Этот тест проверяет, что Allure отчеты генерируются корректно")
    public void testAllureReporting() {
        assertTrue("Allure должен работать", true);
    }
}
