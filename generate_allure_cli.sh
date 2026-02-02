#!/bin/bash

# Проверяем установлен ли Allure
if ! command -v allure &> /dev/null; then
    echo "Allure не установлен. Устанавливаем..."
    
    # Скачиваем Allure
    wget https://github.com/allure-framework/allure2/releases/download/2.24.0/allure-2.24.0.tgz
    
    # Распаковываем
    tar -xzf allure-2.24.0.tgz
    
    # Перемещаем в /usr/local/bin (требует sudo)
    sudo mv allure-2.24.0/bin/allure /usr/local/bin/
    sudo mv allure-2.24.0/lib /usr/local/
    
    # Очищаем
    rm -rf allure-2.24.0*
    echo "Allure установлен"
fi

# Запускаем генерацию отчета
./generate_report.sh
