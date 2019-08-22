# API | 前台-商品模块

## 查看商品详情

- 接口说明：无法查询已下架商品的详细信息
- **GET** `/product/detail.do`
- request

  ```json
  {
    "id": 1111111
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "id": 1111111,
      "categoryId": 100016,
      "name": "洽洽经典葵花籽",
      "subtitle": "五折大促",
      "detail": "308g-1 袋 五香味",
      "price": 10.2,
      "stock": 10,
      "mainImage": "guazi.jpg",
      "subImages": ["guazi.jpg","guazi2.jpg"],
      "imageHost": "http://file.epoch.fun/mall/"
    }
  }
  // error
  {
    "code": 404,
    "msg": "商品已经下架或删除"
  }
  ```

## 搜索商品

- 接口说明：无法查询已下架商品
- **GET** `/product/search.do`
- request

  ```json
  {
    "categoryId": 0,    // 可选          类别查找 (商品类别 id)
    "keyword": "guazi", // 可选          模糊查找 (商品名称关键字)
    "pageNum": 1,       // 可选，默认 1  指定查询页码
    "pageSize": 2       // 可选，默认 5  每页展示数量
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
          "categoryId": 100016,
          "name": "甘源瓜子仁",
          "subtitle": "五折大促",
          "price":12.2,
          "status": 1,
          "mainImage": "guazi.jpg",
          "imageHost": "http://file.epoch.fun/mall/"
        }
      ]
      // 省略其他分页信息
    }
  }
  ```
