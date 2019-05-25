# 配置最小开发环境：
```
MySQL
Redis
JDK1.8或以上
Maven
Nodejs
微信开发者工具
```

# 数据库配置
```
数据库依次导入db/sql下的数据库文件
psys.sql
```

# 参数配置
```
在admin-api/src/main/resources/application.properties中修改
## Redis服务器地址
spring.redis.host=
## Redis服务器连接端口
spring.redis.port=
```

## 微信小程序，根据自己的小程序appId和appSecret配置
```
在core/src/main/resources/application-core.properties中修改
psys.wx.appId=
psys.wx.appSecret=
```

## 腾讯服务器,项目默认服务器可能过期，请手动配置
```
在core/src/main/resources/application-core.properties中修改
psys.storage.tencent.secretId=
psys.storage.tencent.secretKey=
psys.storage.tencent.region=
psys.storage.tencent.bucketName=
```

## 快递鸟快递，如有需要可手动配置
```
在core/src/main/resources/application-core.properties中修改
psys.express.EBusinessID=1471961
psys.express.appKey=1306e742-9f81-4be1-8a19-a0c9dc054bd0
```

## mysql数据库，请根据自己的电脑自行配置
```
在core/src/main/resources/application-core.properties中修改
spring.datasource.url=jdbc:mysql://localhost:3306/psys?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true&verifyServerCertificate=false&useSSL=false
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=12345678
```

# 服务启动
## 启动小商场和管理后台的后端服务
```
本地编译器如IDEA，运行psys/all/main/org/ye/psys/all/AllApplication.java文件
```

## 启动管理后台前端
```
打开命令行，输入以下命令

cd psys/admin
npm install
npm install --registry=https://registry.npm.taobao.org
npm run dev
此时，浏览器打开，输入网址http://localhost:9527, 此时进入管理后台登录页面
```

## 启动小商城前端

```
微信开发工具导入wx项目;
项目配置，启用“不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书”
点击“编译”，即可在微信开发工具预览效果
```



