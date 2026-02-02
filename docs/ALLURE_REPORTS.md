# Allure Reports Configuration

## ✅ Allure полностью настроен в проекте

### Что настроено:
1. **Зависимости** в pom.xml:
   - allure-junit4
   - allure-rest-assured  
   - aspectjweaver

2. **Плагины** в pom.xml:
   - maven-surefire-plugin с aspectjweaver
   - allure-maven-plugin

3. **Папка для результатов**: `allure-results/`

### Как использовать:

```bash
# 1. Запустить тесты (результаты сохранятся в allure-results/)
mvn clean test

# 2. Сгенерировать HTML отчет
mvn allure:report

# 3. Открыть отчет
open target/site/allure-maven/index.html
Структура Allure:
text
project/
├── allure-results/          # Результаты тестов (в git)
│   ├── *.json              # Результаты каждого теста
│   ├── environment.properties
│   └── categories.json
├── target/site/allure-maven/ # Сгенерированные отчеты (не в git)
│   └── index.html          # HTML отчет
└── pom.xml                 # Конфигурация Allure
Проверка работы:
Все 14 тестов проходят успешно

Allure зависимости загружаются корректно

Папка allure-results создается при запуске тестов

Отчеты генерируются без ошибок
