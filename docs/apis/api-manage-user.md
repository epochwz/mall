# API | 后台-用户模块

**接口统一校验规则**

- `400` 非法参数
  - `username` 1-20 个合法字符 (中文、字母、数字、下划线、中划线)
  - `password` 6-32 个字符

## 登录

- **POST** `/manage/user/login.do`
- request

  ```json
  {
    "username": "epoch",
    "password": "epoch_pass"
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "id": 1000000,
      "username": "epoch"
    }
  }
  // error
  {
    "code": 403,
    "msg": "无访问权限 (不是管理员账号)"
    
    // "code": 401,
    // "msg": "密码错误"

    // "code": 404
    // "msg": "用户名不存在"
  }
  ```

## 登出 (退出登录)

- **GET** `/manage/user/logout.do`
- request (从 session 中获取登录信息)
- response

  ```json
  {
    "code": 200
  }
  ```

## 重置密码 (已登录状态)

- **POST** `/manage/user/reset_password.do`
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
    // "msg": "无访问权限 (不是管理员账号)"
    
    // "code": 999,
    // "msg": "未登录"
  }
  ```
