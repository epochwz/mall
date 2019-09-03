# API | 前台-收货地址

**接口统一校验规则**

- `999` 未登录
- `403` 无权限 (不是消费者账号)

## 查看收货地址详情

- **GET** `/shipping/detail.do`
- request

  ```json
  {
    "id": 1
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": {
      "id": 1,
      "userId": 1000000,
      "name": "梦无涯",
      "mobile": "15625172333",
      "province": "广东省",
      "city": "广州市",
      "district": "小谷围街道",
      "address": "宇宙工业大学",
      "zip": "510006"
    }
  }
  // error
  {
    "code": 403,
    "msg": "无权限 (收货地址不属于当前用户)"
    
    // "code": 404,
    // "msg": "收货地址不存在"
  }
  ```

## 查询收货地址列表

- **GET** `/shipping/list.do`
- request

  ```json
  {
    "pageNum": 1,       // 可选，默认 1    指定查询页码
    "pageSize": 2       // 可选，默认 5    每页展示数量
  }
  ```

- response

  ```json
  {
    "code": 200,
    "data":{
      "list":[
        {
          "id": 1,
          "userId": 1000000,
          "name": "梦无涯",
          "mobile": "15625172333",
          "province": "广东省",
          "city": "广州市",
          "district": "小谷围街道",
          "address": "宇宙工业大学",
          "zip": "510006"
        }
      ]
      // 省略其他分页信息
    }
  }
  ```

## 添加收货地址

- **POST** `/shipping/add.do`
- request

  ```json
  {
    "name": "epoch",          // 收货人姓名
    "mobile": "15625172333",  // 手机号码  
    "province": "GuangDong",  // 省份
    "city": "GuangZhou",      // 城市
    "district": "district",   // 街道
    "address": "detail",      // 详细地址
    "zip": "510006"           // 邮编
  }
  // 参数校验规则
  // 非空检查：除了 zip 之外全部非空   
  // mobile 必须符合手机统一校验规则
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": 1 // 新增收货地址的 id
  }
  // error
  {
    "code": 500
  }
  ```

## 删除收货地址

- **POST** `/shipping/delete.do`
- request

  ```json
  {
    "id": 1
  }
  ```
- response

  ```json
  // success
  {
    "code": 200
  }
  // error
  {
    "code": 403,
    "msg": "无权限 (收货地址不属于当前用户)"
    
    // "code": 404,
    // "msg": "收货地址不存在"
  }
  ```

## 修改收货地址

- **POST** `/shipping/update.do`
- request

  ```json
  {
    "id": 1,
    "name": "梦无涯",
    "mobile": "15625172333",
    "province": "广东省",
    "city": "广州市",
    "district": "小谷围街道",
    "address": "宇宙工业大学",
    "zip": "510006"
  }
  // 参数校验规则
  // mobile 非空时符合手机统一校验规则
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "id": 1,
      "userId": 1000000,
      "name": "梦无涯",
      "mobile": "15625172333",
      "province": "广东省",
      "city": "广州市",
      "district": "小谷围街道",
      "address": "宇宙工业大学",
      "zip": "510006"
    }
  }
  // error
  {
    "code": 403,
    "msg": "无权限 (收货地址不属于当前用户)"
    
    // "code": 404,
    // "msg": "收货地址不存在"
  }
  ```
