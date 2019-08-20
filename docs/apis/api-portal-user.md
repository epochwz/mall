# API | 前台-用户模块

**接口统一校验规则**

- `400` 非法参数
  - `username` 1-20 个合法字符 (中文、字母、数字、下划线、中划线)
  - `password` 6-32 个字符
  - `email` 合法的邮箱格式
  - `mobile` 合法的手机号码格式

## 注册

- **POST** `/user/register.do`
- request

  ```json
  {
    "username": "epoch",
    "password": "epoch_pass",
    "email": "epoch.wz@gmail.com",  // 可选，邮箱
    "mobile": "15626272333",        // 可选，手机号码
    "question": "question",         // 可选，密保答案
    "answer": "answer"              // 可选，密保问题
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": 1000000 // 新增用户的 ID
  }
  // error
  {
    "code": 409,
    "msg": "用户名 / 邮箱 / 手机号码 已存在"
  }
  ```

## 账号校验

- 接口说明：校验账号是否已存在
- **GET** `/user/account_verify.do`
- request

  ```json
  {
    "account": "epoch", // 账号 (用户名 / 邮箱 / 手机号码)
    "type": "username"  // 账号类型
    // "account": "epoch.wz@gmail.com",
    // "type": "email"
    // "account": "15626272333",
    // "type": "mobile"
  }
  // 参数校验规则
  // type     是否是系统支持的账号类型
  // account  是否符合相应账号类型的账号统一校验规则
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "msg": "用户名 / 邮箱 / 手机号码 可使用"
  }
  // error
  {
    "code": 409,
    "msg": "用户名 / 邮箱 / 手机号码 已存在"
  }
  ```

## 登录

- **POST** `/user/login.do`
- request

  ```json
  {
    "account": "epoch",
    "type": "username",
    "password": "epoch_pass"
    // "account": "epoch.wz@gmail.com",
    // "type": "email"
    // "account": "15626272333",
    // "type": "mobile"
  }
  // 参数校验规则
  // password 是否符合密码统一校验规则
  // type     是否是系统支持的账号类型
  // account  是否符合相应账号类型的账号统一校验规则
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "id": 1000000,
      "username": "epoch",
      "email": "epoch.wz@gmail.com",
      "mobile": "15626272333",
      "question": "question",
      "role": 1
    }
  }
  // error
  {
    "code": 403,
    "msg": "无访问权限 (不是消费者账号)"
    
    // "code": 401,
    // "msg": "密码错误"

    // "code": 404
    // "msg": "用户名 / 邮箱 / 手机号码 不存在"
  }
  ```

## 登出 (退出登录)

- **GET** `/user/logout.do`
- request (从 session 中获取登录信息)
- response

  ```json
  {
    "code": 200
  }
  ```

## 查看个人信息

- **GET** `/user/info.do`
- request (从 session 中获取登录信息)
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "id": 1000000,
      "username": "epoch",
      "email": "epoch.wz@gmail.com",
      "mobile": "15626272333",
      "question": "question",
      "role": 1
    }
  }
  // error
  {
    "code": 403,
    "msg": "无访问权限 (不是消费者账号)"
    
    // "code": 999,
    // "msg": "未登录"
  }
  ```

## 更新个人信息

- **POST** `/user/update.do`
- request

  ```json
  {
    "id": 1000000,
    "username": "epoch",
    "email": "epoch.wz@gmail.com",
    "mobile": "15626272333",
    "question": "question",
    "answer": "answer"
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "id": 1000000,
      "username": "epoch",
      "email": "epoch.wz@gmail.com",
      "mobile": "15626272333",
      "question": "question",
      "role": 1
    }
  }
  // error
  {
    "code": 409,
    "msg": "用户名 / 邮箱 / 手机号码 已存在"

    // "code": 403,
    // "msg": "无访问权限 (不是消费者账号)"
    
    // "code": 999,
    // "msg": "未登录"
  }
  ```

## 重置密码 (已登录状态)

- **POST** `/user/reset_password.do`
- request

  ```json
  {
    "oldPass": "epoch_pass",
    "newPass": "epoch_pass_new"
  }
  // 参数校验规则
  // oldPass  是否符合密码统一校验规则
  // newPass  是否符合密码统一校验规则
  ```
- response

  ```json
  // success
  {
    "code": 200
  }
  // error
  {
    "code": 401,
    "msg": "旧密码错误"

    // "code": 403,
    // "msg": "无访问权限 (不是消费者账号)"
    
    // "code": 999,
    // "msg": "未登录"
  }
  ```

## 忘记密码 (获取密保问题)

- **GET** `/user/forget_password.do`
- request

  ```json
  {
    "username": "epoch"
  }
  // 参数校验规则
  // username 是否符合账号统一校验规则
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": "question"
  }
  // error
  {
    "code": 404,
    "msg": "未设置密保问题"

    // "code": 404,
    // "msg": "用户名不存在"
  }
  ```

## 提交答案 (获取重置密码的 Token)

- **POST** `/user/commit_answer.do`
- request

  ```json
  {
    "username": "epoch",
    "question": "question",
    "answer": "answer"
  }
  // 参数校验规则
  // username 是否符合账号统一校验规则
  // question 非空
  // answer   非空
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": "b9f655ed-4d71-473f-abff-f989098ff818" // Token, 后续重置密码时必须提交该 Token 作为认证
  }
  // error
  {
    "code": 401,
    "msg": "密保答案错误"
  }
  ```

## 重置密码 (通过 Token)

- **POST** `/user/reset_password_by_token.do`
- request

  ```json
  {
    "username": "epoch",
    "password": "epoch_password",
    "forgetToken": "b9f655ed-4d71-473f-abff-f989098ff818"
  }
  // 参数校验规则
  // username     是否符合账号统一校验规则
  // password     是否符合密码统一校验规则
  // forgetToken  非空
  ```
- response

  ```json
  // success
  {
    "code": 200
  }
  // error
  {
    "code": 401,
    "msg": "Token 不匹配"
    
    // "code": 404,
    // "msg": "Token 已失效"
  }
  ```
