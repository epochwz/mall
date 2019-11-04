# 《小型 Java 网上购物商城》的设计与实现

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本项目将通过完整的 [需求分析](#需求分析)、[原型设计](#原型设计)、[接口设计](#接口设计)、[数据库设计](#数据库设计)、[项目开发](#项目开发)、[项目上线](#项目上线)、[项目测试](#项目测试) 等步骤，从零开始设计并实现一个 *麻雀虽小，五脏俱全* 的 Java 网上购物商城

## 需求分析

开发一个项目 / 产品，首先要做的就是需求分析。通过深思熟虑的需求分析之后，我们可以明确项目需求，并在此基础上划分系统的功能模块，同时了解项目所涉及的数据信息、数据模型等，作为后续进行原型设计、接口设计、数据库设计的参考依据。

在学习了 [《产品需求文档 (PRD) 介绍》] 之后，我按照自己的理解，将需求分析分成以下几个阶段

1. [项目目标分析]：简单说明项目最直观、原始、笼统的需求
2. [简单需求分析]：通过项目的核心功能分析系统的主要流程，明确系统的基本需求，划分基本功能模块
3. [模块功能分析]：按照模块划分，分析模块功能和模块联系
4. [模块数据分析]：按照模块功能，分析模块功能涉及的数据
5. [功能流程分析]：结合模块功能和模块数据，细化各个模块功能的详细流程【类似于用户用例 **(UserCase)** 】

详细的需求分析过程请参考 [《需求分析文档》]

## 原型设计

通过需求分析后，我们已经对整个系统有了深入了解：有什么模块、有什么功能、涉及哪些数据、各个功能之间如何联动等等。在绘制各个模块功能的详细流程图时，甚至已经确定了大部分功能的交互逻辑，剩下的只是如何在页面上整合、展示这些功能，以提供更好的用户体验。

此时，我们已经能够大概想象出整个系统的模样，而 **原型设计** 就是*借助原型工具，将这种想象变成具体可视化的系统原型*。通过系统原型的设计，不仅能够展示系统的雏形，还可以验证并改进之前所做的需求分析的可行性、完整性等。

网站的原型设计通常采用 Axure 工具，感兴趣的小伙伴可以学习 [《Axure 入门初体验》]

## 接口设计

接口设计是一个项目的关键部分，在整个项目的开发过程中起到承上启下的作用（上承功能设计，下启代码实现），而接口文档则是前端和后端在代码实现阶段进行沟通的桥梁

通过原型设计之后，我们已经可以近乎完整的呈现出最终的产品 Demo 了。因此结合需求分析中的模块功能详细流程和原型设计，我们可以提炼出整个项目所需要的全部接口。

本项目的接口设计（文档）包含以下几个方面

- [接口总览]：结合需求分析和原型设计，罗列出项目所需要的全部接口，方便后续编写接口详细设计文档 (API)。
- [接口响应状态码设计]：设计接口的通用响应状态码含义
- [接口响应数据结构设计]：设计接口的通用响应数据结构
- [接口详细设计]：详细描述每个接口的 URL、请求参数、请求参数校验规则、响应数据

详细的接口设计过程请参考 [《接口设计文档》]

> ! 完成接口设计之后，整个项目的设计阶段就基本完成了。根据原型设计和接口设计，前端已经可以开始写代码实现了，而后端还需要完成数据库设计。

## 数据库设计

典型的 JavaEE 项目都离不开数据库，数据库表通常对应着 Java 实体类，因此数据库设计可以说是 Java 后端开发的起点。数据库设计的过程，就是结合需求分析和接口设计，然后使用恰当的表结构、约束、关联关系等建立起合适的业务数据模型。

详细的数据库设计过程请参考 [《数据库设计文档》]

## 项目开发

本项目的开发过程主要涉及以下步骤，具体开发过程可以参考 [代码提交记录]

1. 编写设计文档: 需求分析文档、接口设计文档、API、数据库设计文档、数据库表结构 SQL 文件
2. 搭建项目环境: 初始化数据库、搭建 HTTP 图片服务器
3. 项目构建工具: Maven 项目初始化、环境隔离配置、添加项目依赖
4. 集成项目框架: Spring Web MVC、Spring、MyBatis 和 MyBatis PageHelper 等插件
5. 项目配置文件: 项目常量配置、Logback 日志配置、FTP 工具配置、支付宝配置
6. 基础代码实现
   1. 封装常用工具
      - 封装高复用的服务端响应对象 & 响应状态码枚举类
      - 封装文本工具、日期时间工具、MD5 加密工具
      - 封装配置工具、文件工具、缓存工具
      - 封装单元测试工具、集成测试工具
   2. 生成基础代码：配置 MyBatis Generator 插件，生成项目实体类和持久层代码
   3. 添加公共代码
      - 初始化系统业务状态码
      - 封装系统全局异常处理
      - 配置登录和权限拦截器
      - 初始化持久层通用代码
7. 业务代码实现：实现各个模块控制层、业务层、数据层代码
8. 代码单元测试：在实现各个模块功能时进行独立的单元测试
9. 代码集成测试：在实现各个模块功能后进行自动化集成测试

## 项目上线

我在 [项目上线文档] 中详细记录了本项目的上线部署过程。当然，秉着偷懒的原则，我也为此编写了便捷的自动化部署脚本，使用步骤如下

1. 初始化相关路径变量

   ```bash
   export ENV_FILE=$HOME/.bash_aliases

   echo "export MALL_PATH=$HOME/srccode/mall" >> $ENV_FILE
   echo "export MALL_CONF_DEMO=\$MALL_PATH/demo" >> $ENV_FILE
   echo "export MALL_CONF_PROD=\$MALL_PATH/prod" >> $ENV_FILE
   echo "export MALL_BIN=\$MALL_CONF_DEMO/bin" >> $ENV_FILE
   
   source $ENV_FILE
   ```

2. 克隆项目源码

   ```bash
   git clone https://github.com/epochwz/mall.git $MALL_PATH
   ```

3. 修改配置文件：出于隐私安全考虑，需要使用独立的目录来存储自定义的生产环境配置
   1. 拷贝示例配置

      ```bash
      cp -r $MALL_CONF_DEMO $MALL_CONF_PROD
      ```

   2. 按需修改配置
      - `$MALL_CONF_PROD/conf/file.epoch.fun` 修改图片服务器域名
      - `$MALL_CONF_PROD/conf/mall.epoch.fun` 修改项目的访问域名
      - `$MALL_CONF_PROD/resources.pro/ftp.properties` 修改图片服务器 IP
      - `$MALL_CONF_PROD/resources.pro/mall.properties` 修改相关域名
      - `$MALL_CONF_PROD/resources.pro/alipay.properties` 修改支付宝相关配置 (私钥，公钥)

4. 设置虚拟内存 *(可选)*：如果服务器内存低于 `1G`, 则通常需要设置虚拟内存，避免因为内存不足而导致 MySQL 运行失败

   ```bash
   # 设置虚拟内存，大小为 512M, 可根据需要进行调整
   bash $MALL_BIN/swap.sh enable 512
   ```

   脚本执行完毕后，需要根据提示重启系统，或者之后手动重启使之生效

5. 搭建运行环境

   *温馨提示*：脚本最后在安装 & 配置 MySQL 时，需要手动进行命令行交互，比如设置账号、密码等

   ```bash
   bash $MALL_BIN/init.sh all
   ```

6. 部署项目代码 (以后每次需要更新代码时，只需要执行以下命令即可)

   ```bash
   bash $MALL_BIN/mall.sh
   ```

## 项目测试

- 单元测试：本项目使用 `JUnit` + `Mockito` 对各个模块功能的控制层、业务层分别进行了单元测试
- 集成测试：本项目使用 `JUnit` + `DbSetup` + `Spring Test` 对各个模块功能进行自动化集成测试
- 手动测试：本项目使用 `Restlet Client` 对各个模块功能、文件上传功能、支付功能进行人工测试
  - 可以下载本项目的 [测试文件]，并使用 [Restlet Client] 进行导入，从而方便地对相关接口进行测试

    ![import-test-data1] ![import-test-data2] ![import-test-data3]

## TODO

- [ ] 原型设计
- [ ] 前端实现

[《产品需求文档 (PRD) 介绍》]:https://tangjie.me/blog/111.html
[《Axure 入门初体验》]:https://www.imooc.com/learn/795
[《需求分析文档》]:/docs/design/需求分析文档.md
[项目目标分析]:/docs/design/需求分析文档.md#项目目标分析
[简单需求分析]:/docs/design/需求分析文档.md#简单需求分析
[模块功能分析]:/docs/design/需求分析文档.md#模块功能分析
[模块数据分析]:/docs/design/需求分析文档.md#模块数据分析
[功能流程分析]:/docs/design/需求分析文档.md#功能流程分析
[《接口设计文档》]:/docs/design/接口设计文档.md
[接口响应状态码设计]:/docs/design/接口设计文档.md#接口响应状态码设计
[接口响应数据结构设计]:/docs/design/接口设计文档.md#接口响应数据结构设计
[接口总览]:/docs/design/接口设计文档.md#接口总览
[接口详细设计]:/docs/design/接口设计文档.md#接口详细设计
[《数据库设计文档》]:/docs/design/数据库设计文档.md
[代码提交记录]: https://github.com/epochwz/mall/commits/master
[项目上线文档]: /docs/deploy/项目上线文档.md
[Restlet Client]: https://chrome.google.com/webstore/detail/restlet-client-rest-api-t/aejoelaoggembcahagimdiliamlcdmfm
[测试文件]: https://raw.githubusercontent.com/epochwz/mall/master/docs/assets/mall.json
[import-test-data1]: /docs/images/import-test-data1.png
[import-test-data2]: /docs/images/import-test-data2.png
[import-test-data3]: /docs/images/import-test-data3.png
