# Maven学习总结

![1543213743325](C:\Users\guoming.zhang\AppData\Roaming\Typora\typora-user-images\1543213743325.png)



mvn  archetype:generate  5可以快速产生maven项目骨架



## 常用命令一

1、mvn compile

mvn test-compile

mvn test  编译源文件和测试文件

mvn package  打包  生成target

mvn install  部署到本地仓库

mvn deploy   部署到远程仓库

mvn clean

mvn help:effective-pom  窥探SuperPom

## 常用命令二

mvn  dependency:tree  依赖树

mvn  clean  package -Pdev -Dmaven.test.skip=true  发布时过滤测试代码    关联dev环境

mvn  -U clean package -Pbeta -Dmaven.test.skip=true

## maven生命周期

三个标准生命周期：

clean LifeCycle  构建之前进行清理工作：

Pre-clean

Clean

Post-clean



default(or build)  LifeCycle 编译，测试，打包，部署 :  23个阶段:

1.pre-resources

2.compile

3.test-compile

4.test

5.package

6.install

7.deploy



site LifeCycle 生成发布项目站点:

Pre-site

Site

Post-site

Site-deploy

## Maven仓库

groupId/artifactId/version/artifactId-version.packaging

本地仓库，中央仓库，私服，其他远程仓库

## Maven依赖

type默认为jar、scope范围、optional依赖是否可选、exclusions排除传递依赖

scope:

compile:默认值

test:测试依赖范围

provided:已提供依赖范围

runtime:运行时依赖范围

解决依赖冲突：

短路径优先原则、若相同长度路径，谁先声明，谁被解析

对版本依赖归类

