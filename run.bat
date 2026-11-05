@echo off
title TP-157-Spring-Boot-Swagger
chcp 65001 >nul
mode con: cols=120 lines=55
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "MAVEN_OPTS=-Dfile.encoding=UTF-8"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"

:menu
cls
echo ==============================================================================
echo   TP 6 - Spring Boot et Swagger : API de gestion des etudiants
echo   API REST, Spring Data JPA, JUnit 5 + Mockito, springdoc-openapi
echo   Spring Boot 3.2.3  /  MySQL 8  /  JDK 17
echo ==============================================================================
echo.
echo   ATTENTION : le serveur MySQL doit etre demarre
echo               (serveur-mysql.bat dans le dossier TP153-MySQL-Serveur)
echo.
echo   [1] Lancer l'application   (http://localhost:8080)
echo   [2] Suite de tests unitaires et d'integration (mvn test)
echo   [3] Recompiler proprement le projet
echo   [4] Afficher les URLs utiles
echo   [0] Quitter
echo.
set "choix="
set /p "choix=Votre choix : "
if "%choix%"=="1" goto :lancer
if "%choix%"=="2" goto :tests
if "%choix%"=="3" goto :compile
if "%choix%"=="4" goto :urls
if "%choix%"=="0" goto :fin
goto :menu

:lancer
cls
echo ==============================================================================
echo   L'application demarre sur le port 8080.
echo   Appuyez sur Ctrl+C dans cette fenetre pour l'arreter.
echo ==============================================================================
echo.
echo   Swagger UI  : http://localhost:8080/swagger-ui.html
echo   OpenAPI     : http://localhost:8080/v3/api-docs
echo   Etudiants   : http://localhost:8080/students/all
echo.
call mvn -B -q spring-boot:run
goto :suspension

:tests
cls
echo Execution de la suite de tests (Mockito + integration MockMvc)...
echo.
call mvn -B test
if errorlevel 1 goto :echec
goto :suspension

:compile
cls
call mvn -B -q clean compile
if errorlevel 1 goto :echec
echo.
echo Compilation terminee sans erreur.
goto :suspension

:urls
cls
echo ==============================================================================
echo   URLS DE L'API  (l'application doit etre lancee, choix 1)
echo ==============================================================================
echo.
echo   Swagger UI
echo     http://localhost:8080/swagger-ui.html
echo.
echo   Description OpenAPI au format JSON
echo     http://localhost:8080/v3/api-docs
echo.
echo   Endpoints REST
echo     GET     http://localhost:8080/students/all
echo     GET     http://localhost:8080/students/count
echo     GET     http://localhost:8080/students/byYear
echo     GET     http://localhost:8080/students/1
echo     GET     http://localhost:8080/students/nesApres?annee=1990
echo     POST    http://localhost:8080/students/save
echo     PUT     http://localhost:8080/students/update/1
echo     DELETE  http://localhost:8080/students/delete/1
echo.
goto :suspension

:echec
echo.
echo *** Echec de la compilation ou de l'execution. ***
echo Verifiez que le serveur MySQL est demarre sur le port 3306.
:suspension
echo.
echo ==============================================================================
echo   Fin de l'operation. Appuyez sur une touche pour revenir au menu.
echo ==============================================================================
pause >nul
goto :menu

:fin
exit /b 0
