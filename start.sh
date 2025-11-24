#!/bin/bash

# SpringMVC博客文件管理系统启动脚本

echo "=========================================="
echo "  SpringMVC博客文件管理系统"
echo "=========================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请安装JDK 19或更高版本"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请安装Maven 3.6或更高版本"
    exit 1
fi

# 检查MySQL连接
echo "检查MySQL连接..."
mysql -u root -p -e "USE blog_system;" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "警告: 无法连接到MySQL数据库或blog_system数据库不存在"
    echo "请确保："
    echo "1. MySQL服务正在运行"
    echo "2. 已创建blog_system数据库"
    echo "3. 执行了src/main/resources/init.sql初始化脚本"
    echo ""
    read -p "是否继续？(y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 创建上传目录
echo "创建上传目录..."
mkdir -p src/main/webapp/uploads

# 编译项目
echo "编译项目..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "错误: 项目编译失败"
    exit 1
fi

# 打包项目
echo "打包项目..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "错误: 项目打包失败"
    exit 1
fi

echo "=========================================="
echo "  构建完成！"
echo "=========================================="
echo ""
echo "部署说明："
echo "1. 将 target/demo.war 复制到Tomcat的webapps目录"
echo "2. 启动Tomcat服务器"
echo "3. 访问 http://localhost:8080/demo/"
echo ""
echo "默认管理员账号: admin / admin123"
echo ""
echo "如果使用内嵌Tomcat，可以运行："
echo "mvn spring-boot:run (需要添加Spring Boot依赖)"