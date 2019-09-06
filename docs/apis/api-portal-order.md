# API | 前台-订单模块

**接口统一校验规则**

- `999` 未登录
- `403` 无权限 (不是消费者账号)

## 查看订单详情

- **GET** `/order/detail.do`
- request

  ```json
  {
    "orderNo": 1521421465877
  }
  ```

- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "userId": 1000000,                    // 订单所属用户
      "orderNo": 1521421465877,             // 订单号
      
      "payment": 30.6,                      // 订单金额
      "postage": 0,                         // 订单运费
      
      "status": 10,                         // 订单状态
      "statusDesc": "unpaid",               // 订单状态描述信息
      
      "createTime": "2019-07-19 21:31:05",  // 订单创建时间
      
      "paymentType": 1,                     // 支付类型
      "paymentTypeDesc": "online",          // 支付类型描述信息
      "paymentTime": "",                    // 订单支付时间

      "shipTime": "",                       // 订单发货时间
      "endTime": "",                        // 订单完成时间
      "closeTime": "",                      // 订单关闭时间 
      
      "shipping":{                          // 订单收货地址 (快照)
        "id": 1000000,
        "name": "梦无涯",
        "mobile": "15625172333",
        "province": "广东省",
        "city": "广州市",
        "district": "小谷围街道",
        "address": "宇宙工业大学",
        "zip": "510006"
      },
      
      "products":[                          // 订单商品清单 (快照)
        {
          "productId": 1000000,
          "productName": "洽洽经典葵花籽",
          "productImage": "http://file.epoch.fun/mall/order/guazi.jpg",
          "unitPrice": 10.2,
          "quantity": 3,
          "totalPrice": 30.6
        }
      ]
    }
  }
  // error
  {
    "code": 404,
    "msg": "找不到该订单 (该订单不属于当前用户)"
  }
  ```

## 搜索订单

- **GET** `/order/search.do`
- request

  ```json
  {
    "orderNo": 1521421465877,   // 可选           精确查询 (订单号)
    "keyword": "guazi",         // 可选           模糊查询 (订单商品名称关键字)
    "status": 0,                // 可选           状态查询 (订单状态)
    "startTime": "",            // 可选           范围查询 (开始时间)
    "endTime": "",              // 可选           范围查询 (结束时间)
    "pageNum": 1,               // 可选，默认 1   指定查询页码
    "pageSize": 5               // 可选，默认 5   每页展示数量
  }
  ```

- response

  ```json
  {
    "code": 200,
    "data":{
      "list":[
        {
          "userId": 1000001,
          "orderNo": 1521421465877,
          
          "payment": 30.6,
          "postage": 0,
          
          "status": 10,
          "statusDesc": "未支付",
          
          "createTime": "2019-07-19 21:31:05",
          "shippingName": "梦无涯",
          "products":[
            {
              "productId": 1000000,
              "productName": "洽洽经典葵花籽",
              "productImage": "http://file.epoch.fun/mall/order/guazi.jpg",
              "unitPrice": 10.2,
              "quantity": 3,
              "totalPrice": 30.6
            }
          ]
        }
      ]
      // 省略其他分页信息
    }
  }
  ```

## 预览订单

- **GET** `/order/preview.do`
- request
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "payment": 30.6,
      "postage": 0,
      "products":[
        {
          "productId": 1000000,
          "productName": "洽洽经典葵花籽",
          "productImage": "http://file.epoch.fun/mall/guazi.jpg",
          "unitPrice": 10.2,
          "quantity": 3,
          "totalPrice": 30.6
        }
      ]
    }
  }
  // error
  {
    "code": 400,
    "msg": "请选择要购买的商品",
    
    // 404  某商品不存在 / 已下架
    // 400  某商品数量超过限制
  }
  ```

## 创建订单

- **POST** `/order/create.do`
- request

  ```json
  {
    "shippingId": 1 // 收货地址 id
  }
  ```

- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "userId": 1000001,
      "orderNo": 1521421465877,
      
      "payment": 30.6,
      "postage": 0,
      
      "status": 10,
      "statusDesc": "未支付",
      
      "createTime": "2019-07-19 21:31:05",
      
      "paymentType": 1,
      "paymentTypeDesc": "在线支付",
      "paymentTime": "",

      "shipTime": "",
      "endTime": "",
      "closeTime": "", 
      
      "shipping":{
        "id": 1000000,
        "name": "梦无涯",
        "mobile": "15625172333",
        "province": "广东省",
        "city": "广州市",
        "district": "小谷围街道",
        "address": "宇宙工业大学",
        "zip": "510006"
      },
      
      "products":[
        {
          "productId": 1000000,
          "productName": "洽洽经典葵花籽",
          "productImage": "http://file.epoch.fun/mall/order/guazi.jpg",
          "unitPrice": 10.2,
          "quantity": 3,
          "totalPrice": 30.6
        }
      ]
    }
  }
  // error
  {
    "code": 404,
    "msg": "收货地址不存在 (该收货地址不属于当前用户)"
    
    // 400  请选择要购买的商品
    // 400  某商品不存在 / 已下架
    // 400  某商品超过数量限制
  }
  ```

## 取消订单

- **POST** `/order/cancel.do`
- request

  ```json
  {
    "orderNo": 1521399829169
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
    "code": 400,
    "msg": "取消失败：已发货 / 已完成 / 已关闭"
    
    // "code": 404,
    // "msg": "找不到该订单 (该订单不属于当前用户)"
  }
  ```

## 支付订单

- **POST** `/order/pay.do`
- request

  ```json
  {
    "orderNo": 1521401812548,   // 订单号     必填
    "paymentType": 1,           // 支付类型   可选，默认 1
    "paymentPlatform": 1        // 支付平台   可选，默认 1，仅当 paymentType=1 (在线支付) 时才需要传递此参数
  }
  // 参数校验规则
  // paymentType      非空时必须是系统支持的支付类型
  // paymentPlatform  非空时必须是系统支持的支付平台
  ```

- response

  ```json
  // success
  {
    "code": 200,
    "data": {
      "orderNo": "1521401812548",
      "qrCode": "http://file.epoch.fun/mall/QrCode/1521401812548.png"
    }
  }
  // error
  {
    "code": 400,
    "msg": "订单已支付" // 除了未支付状态，其余订单状态均视为已支付

    // "code": 404,
    // "msg": "找不到该订单 (该订单不属于当前用户)"
  }
  ```

## 查询订单支付状态

- **GET** `/order/payment_status.do`
- request

  ```json
  {
    "orderNo": 1521401812548
  }
  ```

- response

  ```json
  // success
  {
    "code": 200,
    "data": true      // 订单已支付
    // "data": false  // 订单未支付
  }
  // error
  {
    "code": 404,
    "msg": "找不到该订单 (该订单不属于当前用户)"
  }
  ```
