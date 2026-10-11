@echo off
echo Starting Spring Boot Backend...
cd /d "%~dp0backend"
call .\mvnw spring-boot:run
pause
