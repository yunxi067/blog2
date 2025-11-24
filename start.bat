@echo off
chcp 65001 >nul
echo ==========================================
echo   SpringMVC博客文件管理系统
echo ==========================================

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请安装JDK 19或更高版本
    pause
    exit /b 1
)

REM 检查Maven环境
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请安装Maven 3.6或更高版本
    pause
    exit /b 1
)

REM 检查MySQL连接（简单检查）
echo 检查MySQL连接...
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 警告: 未找到MySQL客户端
    echo 请确保：
    echo 1. MySQL服务正在运行
    echo 2. 已创建blog_system数据库
    echo 3. 执行了src^/main^/resources^/init.sql初始化脚本
    echo.
)

REM 创建上传目录
echo 创建上传目录...
if not exist "src\main\webapp\uploads" mkdir "src\main\webapp\uploads"

REM 编译项目
echo 编译项目...
call mvn clean compile
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

REM 打包项目
echo 打包项目...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo 错误: 项目打包失败
    pause
    exit /b 1
)

echo ==========================================
echo   构建完成！
echo ==========================================
echo.
echo 部署说明：
echo 1. 将 target\demo.war 复制到Tomcat的webapps目录
echo 2. 启动Tomcat服务器
echo 3. 访问 http://localhost:8080/demo/
echo.
echo 默认管理员账号: admin / admin123
echo.
pause