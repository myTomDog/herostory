英雄传说项目

当前分支保存第六节课的项目代码

浏览器访问新url:
http://cdn0001.afrxvk.cn/hero_story/demo/step030/index.html?serverAddr=127.0.0.1:12345&userId=1

注意！！！mysql数据库请使用8.0.x版本，否则数据库连接将报错，导致数据库无法正常使用。
    
   若您坚持使用5.7.x或5.6.x或5.5.x版本，请按以下操作修改项目配置文件：
1、将pom文件中的mysql-connector-java依赖，版本号从8.0.18改为5.1.8；
2、将MyBatisConfig.xml文件中的driver配置，value值从com.mysql.cj.jdbc.Driver改为com.mysql.jdbc.Driver