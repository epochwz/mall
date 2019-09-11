# API | 后台-订单管理

**接口统一校验规则**

- `999` 未登录
- `403` 无权限 (不是管理员账号)

## 查看订单详情

- **GET** `/manage/order/detail.do`
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
      
      "paymentType": 1,                     // 支付类型
      "paymentTypeDesc": "online",          // 支付类型描述信息
      "paymentTime": "",                    // 订单支付时间

      "createTime": "2019-07-19 21:31:05",  // 订单创建时间
      
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
    "msg": "找不到该订单"
  }
  ```

## 搜索订单

- **GET** `/manage/order/search.do`
- request

  ```json
  {
    "orderNo": 1521399829169,   // 可选           精确查询 (订单号)
    "userId": 1000000,          // 可选           所属用户 (订单所属用户)
    "status": 0,                // 可选           状态查询 (订单状态)
    "startTime": "",            // 可选           范围查询 (开始时间)
    "endTime": "",              // 可选           范围查询 (结束时间)
    "pageNum": 1,               // 可选，默认 1   指定查询页码
    "pageSize": 5               // 可选，默认 5   每页展示数量
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": {
      "list": [
        {
          "userId": 1000001,
          "orderNo": 1521421465877,
          
          "payment": 30.6,
          "postage": 0,
          
          "status": 10,
          "statusDesc": "未支付",
          
          "createTime": "2019-07-19 21:31:05",
          "shipping":{
            "id": 1000000,
            "name": "梦无涯"
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
      ]
      // 省略其他分页信息
    }
  }
  ```

## 订单发货

- **POST** `/manage/order/ship.do`
- request

  ```json
  {
    "orderNo": 1521421812548
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "msg": "发货成功"
  }
  // error
  {
    "code": 400,
    "msg": "发货失败：已取消 / 未付款 / 已完成 / 已关闭"

    // "code": 404
  }
  ```

## 关闭订单

- **POST** `/manage/order/close.do`
- request

  ```json
  {
    "orderNo": 1521421812548
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "msg": "关闭订单成功"
  }
  // error
  {
    "code": 400,
    "msg": "关闭订单失败：已取消 / 已付款 / 已发货 / 已完成"

    // "code": 404
  }
  ```
