#!/bin/bash
echo "📊 Генерация Allure отчета через CLI..."

# Проверяем установлен ли allure
if ! command -v allure &> /dev/null; then
    echo "❌ Allure CLI не установлен"
    echo "📦 Установите: brew install allure (macOS)"
    echo "   или скачайте с https://github.com/allure-framework/allure2/releases"
    exit 1
fi

# Очищаем
rm -rf target/allure-results target/allure-report

# Запускаем тесты
echo "🔧 Запуск тестов..."
mvn test -Dallure.results.directory=target/allure-results

# Генерируем отчет через allure CLI
echo "📈 Генерация отчета..."
allure generate target/allure-results -o target/allure-report --clean

if [ -d "target/allure-report" ]; then
    echo "✅ Отчет создан: target/allure-report/index.html"
    echo ""
    echo "🚀 Для открытия отчета выполните:"
    echo "   allure open target/allure-report"
    echo "   или откройте в браузере: file://$(pwd)/target/allure-report/index.html"
else
    echo "❌ Ошибка при создании отчета"
fi
