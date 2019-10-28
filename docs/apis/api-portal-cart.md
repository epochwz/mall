# API | 前台-购物车模块

**接口统一校验规则**

- `999` 未登录
- `403` 无权限 (不是消费者账号)

## 查看购物车

- **GET** `/cart/list.do`
- request
- response

  ```json
  {
    "code": 200,
    "data":{
      "imageHost": "http://file.epoch.fun/mall/", // 图片 URL 前缀
      "cartTotalPrice": 24.4,       // 购物车商品总价
      "allChecked": true,           // 购物车商品全选状态
      "cartItems":[                 // 购物车商品清单
        {
          "productId": 1,           // 商品 id
          "productName": "guazi",   // 商品名称
          "productImage": "xx.jpg", // 商品大图
          "unitPrice": 12.2,        // 商品单价
          "quantity": 2,            // 商品数量
          "totalPrice": 24.4,       // 商品总价
          "checked": true,          // 商品选中状态
          "limit": false            // 商品是否超出数量限制
        }
      ]
    }
  }
  ```

## 查询购物车商品数量

- 接口说明：购物车中各个商品数量的总和
- **GET** `/cart/count.do`
- request
- response

  ```json
  {
    "code": 200,
    "data": 2
  }
  ```

## 添加购物车商品

- **POST** `/cart/add.do`
- request

  ```json
  {
    "productId": 2, // 必填，商品 id
    "count": 10     // 可选，商品数量，默认 1
  }
  // 参数校验规则
  // count 大于 0
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "imageHost": "http://file.epoch.fun/mall/",
      "cartTotalPrice": 24.4,
      "allChecked": true,
      "cartItems":[       
        {
          "productId": 1,
          "productName": "guazi",
          "productImage": "xx.jpg",
          "unitPrice": 12.2,
          "quantity": 2,
          "totalPrice": 24.4,
          "checked": true,
          "limit": false       
        }
      ]
    }
  }
  ```

## 删除购物车商品

- **POST** `/cart/delete.do`
- request

  ```json
  {
    "productIds": [1,2]
  }
  ```
- response

  ```json
  {
    "code": 200,
    "data":{
      "imageHost": "http://file.epoch.fun/mall/",
      "cartTotalPrice": 24.4,
      "allChecked": true,
      "cartItems":[       
        {
          "productId": 1,
          "productName": "guazi",
          "productImage": "xx.jpg",
          "unitPrice": 12.2,
          "quantity": 2,
          "totalPrice": 24.4,
          "checked": true,
          "limit": false       
        }
      ]
    }
  }
  ```

## 修改购物车商品数量

- **POST** `/cart/update.do`
- request

  ```json
  {
    "productId": 1,
    "count": 2
  }
  // 参数校验规则
  // count 大于 0
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "imageHost": "http://file.epoch.fun/mall/",
      "cartTotalPrice": 24.4,
      "allChecked": true,
      "cartItems":[       
        {
          "productId": 1,
          "productName": "guazi",
          "productImage": "xx.jpg",
          "unitPrice": 12.2,
          "quantity": 2,
          "totalPrice": 24.4,
          "checked": true,
          "limit": false       
        }
      ]
    }
  }
  ```

## 勾选 / 取消勾选

- **POST** `/cart/check.do`
- request

  ```json
  {
    "productId": 1,
    "checked": false
  }
  ```
- response

  ```json
  {
    "code": 200,
    "data":{
      "imageHost": "http://file.epoch.fun/mall/",
      "cartTotalPrice": 24.4,
      "allChecked": true,
      "cartItems":[       
        {
          "productId": 1,
          "productName": "guazi",
          "productImage": "xx.jpg",
          "unitPrice": 12.2,
          "quantity": 2,
          "totalPrice": 24.4,
          "checked": true,
          "limit": false       
        }
      ]
    }
  }
  ```

## 全选 / 全不选

- **POST** `/cart/check_all.do`
- request

  ```json
  {
    "checked": false
  }
  ```
- response

  ```json
  {
    "code": 200,
    "data":{
      "imageHost": "http://file.epoch.fun/mall/",
      "cartTotalPrice": 24.4,
      "allChecked": true,
      "cartItems":[       
        {
          "productId": 1,
          "productName": "guazi",
          "productImage": "xx.jpg",
          "unitPrice": 12.2,
          "quantity": 2,
          "totalPrice": 24.4,
          "checked": true,
          "limit": false       
        }
      ]
    }
  }
  ```
