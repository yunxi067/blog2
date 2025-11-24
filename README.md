# SpringMVC博客文件管理系统

一个基于SpringMVC + MyBatis的完整文件管理系统，包含用户管理、文件上传下载、空间管理、关注通知等功能。

## 功能特性

### 阶段三：用户与空间基础功能
- ✅ 用户注册/登录/角色管理
- ✅ 空间管理（查询、冻结/解冻、空间统计）
- ✅ 事务处理（注册时同时创建用户和空间）

### 阶段四：文件上传/管理/下载
- ✅ 文件上传（支持多文件类型）
- ✅ 文件列表与分类管理
- ✅ 文件下载与统计
- ✅ 空间容量检查

### 阶段五：扩容申请与管理员后台
- ✅ 扩容申请功能
- 🚧 管理员后台（开发中）

### 阶段六：加分功能
- 🚧 热力榜/关注/在线预览（开发中）
- ✅ 关注功能基础架构
- ✅ 通知系统基础架构

### 阶段七：安全与优化
- ✅ 登录拦截器
- ✅ 权限控制
- ✅ 异常处理

## 技术栈

- **后端**: SpringMVC 6.1.5 + MyBatis 3.5.15
- **数据库**: MySQL 8.0
- **连接池**: Druid 1.2.20
- **前端**: Bootstrap 5 + JSP + JavaScript
- **文件上传**: Commons FileUpload
- **JSON处理**: Jackson 2.17.0
- **构建工具**: Maven
- **Java版本**: 19

## 项目结构

```
src/
├── main/
│   ├── java/com/itzhihao/blog/
│   │   ├── controller/     # 控制器层
│   │   ├── service/        # 业务逻辑层
│   │   ├── mapper/         # MyBatis映射器接口
│   │   ├── entity/         # 实体类
│   │   ├── common/         # 通用类
│   │   ├── util/           # 工具类
│   │   └── interceptor/    # 拦截器
│   ├── resources/
│   │   ├── mapper/         # MyBatis XML映射文件
│   │   ├── db.properties   # 数据库配置
│   │   ├── mybatis-config.xml
│   │   └── init.sql        # 数据库初始化脚本
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── views/      # JSP页面
│       │   ├── spring-mvc.xml
│       │   ├── spring-mybatis.xml
│       │   └── web.xml
│       ├── js/             # JavaScript文件
│       └── uploads/        # 文件上传目录
```

## 快速开始

### 1. 环境要求
- JDK 19+
- Maven 3.6+
- MySQL 8.0+
- Tomcat 10+ (或使用内嵌服务器)

### 2. 数据库配置
1. 创建MySQL数据库
2. 执行 `src/main/resources/init.sql` 初始化数据库
3. 修改 `src/main/resources/db.properties` 中的数据库连接信息

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/blog_system?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8
db.username=root
db.password=your_password
```

### 3. 编译运行
```bash
# 编译项目
mvn clean compile

# 打包项目
mvn clean package

# 运行项目（需要部署到Tomcat）
# 将 target/demo.war 部署到Tomcat的webapps目录
```

### 4. 访问系统
- 访问地址: http://localhost:8080/demo/
- 默认管理员账号: admin / admin123

## 主要功能说明

### 用户管理
- 用户注册：创建用户时自动分配100MB空间
- 用户登录：支持用户名/密码登录
- 角色管理：普通用户和管理员两种角色
- 账户状态：正常/冻结状态

### 文件管理
- 文件上传：支持多种文件类型，自动分类存储
- 文件列表：按用户展示所有文件，支持分类筛选
- 文件下载：权限检查，统计下载次数
- 文件操作：删除、重命名、冻结等

### 空间管理
- 空间查看：显示总空间、已用空间、剩余空间
- 空间扩容：用户可申请扩容，管理员审核
- 空间状态：管理员可冻结/解冻用户空间

### 关注通知
- 关注功能：用户可关注其他用户
- 通知系统：新文件上传、新关注等通知
- 消息提醒：未读消息计数显示

## API接口

### 用户接口
- POST `/user/register` - 用户注册
- POST `/user/login` - 用户登录
- POST `/user/logout` - 用户退出
- GET `/user/info` - 获取用户信息
- GET `/user/space` - 获取用户空间信息

### 文件接口
- POST `/file/upload` - 文件上传
- GET `/file/list` - 获取文件列表
- GET `/file/info/{fileId}` - 获取文件信息
- POST `/file/delete/{fileId}` - 删除文件
- GET `/download/file/{fileId}` - 下载文件

## 数据库表设计

### 主要表结构
- `user` - 用户表
- `space` - 用户空间表
- `file_info` - 文件信息表
- `space_apply` - 扩容申请表
- `follow` - 关注关系表
- `notification` - 通知表

## 安全特性

- 密码MD5加密存储
- 登录状态验证
- 文件访问权限控制
- SQL注入防护（MyBatis预编译）
- XSS防护（前端输入验证）

## 待完善功能

- [ ] 管理员后台完整实现
- [ ] 热门排行榜功能
- [ ] 在线文件预览
- [ ] 邮件通知功能
- [ ] 文件分享功能
- [ ] 批量文件操作
- [ ] 更详细的日志记录

## 注意事项

1. 文件上传目录 `uploads` 需要有写入权限
2. 建议在生产环境中配置文件大小限制和类型限制
3. 定期清理临时文件和过期通知
4. 数据库连接池配置可根据实际需求调整

## 许可证

MIT License