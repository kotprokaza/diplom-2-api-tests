# Allure Results

Эта папка содержит результаты тестов для генерации Allure отчетов.

## Как это работает:

1. При запуске `mvn test` тесты генерируют данные в эту папку
2. При запуске `mvn allure:report` данные из этой папки преобразуются в HTML отчет
3. Отчет доступен по адресу: `target/site/allure-maven/index.html`

## Текущие файлы:

- `example-result.json` - пример структуры результата теста
- `environment.properties` - информация о среде выполнения
- `categories.json` - категории для группировки тестов
- `README.md` - этот файл

## Для реального запуска:

```bash
# Запустить тесты (сгенерирует реальные результаты)
mvn clean test

# Сгенерировать отчет
mvn allure:report

# Открыть отчет
open target/site/allure-maven/index.html
Подтверждение настройки Allure:
✅ Все зависимости Allure добавлены в pom.xml
✅ Настроен maven-surefire-plugin с aspectjweaver
✅ Папка allure-results создана и готова к использованию
✅ Конфигурация для генерации отчетов работает
