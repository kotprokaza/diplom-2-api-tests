#!/bin/bash
echo "Генерация отчета Allure..."
allure generate allure-results -o allure-report --clean
echo "Запуск веб-сервера с отчетом..."
allure open allure-report
