# 交易引擎
单机版Java交易引擎。

## 准备工作
- Jdk1.8
- MySQL8
- Maven3
- Git
- Redis

## 快速开始
```
# 初始化MySQL脚本
MySQL8> source resources/schema.sql

# 打包
mvn clean package

# 运行
java -jar target/*.jar
```

## 配置
- MySQL用户名和密码。
- Redis用户名和密码。
- 生产环境日志文件路径。



