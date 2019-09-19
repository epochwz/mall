# API | 后台-商品管理

**接口统一校验规则**

- `999` 未登录
- `403` 无权限 (不是管理员账号)

## 查看商品详情

- **GET** `/manage/product/detail.do`
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
      "categoryId": 1000016,
      "name": "洽洽经典葵花籽",
      "subtitle": "五折大促",
      "mainImage": "guazi.jpg",
      "subImages": ["guazi.jpg","guazi2.jpg"],
      "detail": "308g-1 袋 五香味",
      "price": 10.2,
      "stock": 10,
      "status": 1,
      "imageHost": "http://file.epoch.fun/mall/"
    }
  }
  // error
  {
    "code": 404,
    "msg": "找不到商品"
  }
  ```

## 搜索商品

- **GET** `/manage/product/search.do`
- request

  ```json
  {
    "productId": 1111111,   // 可选          精确查找 (商品 id)
    "categoryId": 0,        // 可选          类别查找 (商品类别 id)
    "keyword": "guazi",     // 可选          模糊查找 (商品名称关键字)
    "pageNum": 1,           // 可选，默认 1  当前展示页码
    "pageSize": 2           // 可选，默认 5  每页展示数量
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data":{
      "list":[
        {"id": 1, "categoryId": 1000016, "name": "甘源瓜子仁", "subtitle": "五折大促","price":12.2,"mainImage": "guazi.jpg", "status": 1,"imageHost": "http://file.epoch.fun/mall/"},
        {"id": 2, "categoryId": 1000016, "name": "绿茶瓜子仁", "subtitle": "五折大促","price":12.2,"mainImage": "guazi2.jpg", "status": 1, "imageHost": "http://file.epoch.fun/mall/"}
      ]
      // 省略其他分页信息
    }
  }
  ```

## 添加商品

- **POST** `/manage/product/add.do`
- request

  ```json
  {
    "categoryId": 1000000,                    // 必填，商品类别
    "name": "guazi",                          // 必填，商品名称
    "price": 10.9,                            // 必填，商品价格
    "stock": 50,                              // 可选，商品库存      默认 0
    "status": 1,                              // 可选，商品销售状态  默认 1
    "subtitle": "subtitle",                   // 可选，商品副标题
    "detail": "detail",                       // 可选，商品详情
    "mainImage": "guazi.jpg",                 // 可选，商品大图      默认是商品组图的第一张
    "subImages": ["guazi.jpg","guazi2.jpg"]   // 可选，商品套图
  }
  // 参数校验规则
  // name   非空
  // price  非空且必须大于 0
  // stock  非空时必须大于等于 0
  // status 非空时必须是系统支持的销售状态
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": 1111111 // 新增商品的 id
  }
  // error
  {
    "code": 404,
    "msg": "商品类别不存在 / 已弃用"
  }
  ```

## 更新商品

- **POST** `/manage/product/update.do`
- request

  ```json
  {
    "id": 1111111,
    "categoryId": 1000016,
    "name": "洽洽经典葵花籽",
    "price": 10.2,
    "stock": 10,
    "status": 1,
    "subtitle": "五折大促",
    "mainImage": "guazi.jpg",
    "subImages": ["guazi.jpg","guazi2.jpg"],
    "detail": "308g-1 袋 五香味"
  }
  // 参数校验规则
  // price  非空时必须大于 0
  // stock  非空时必须大于等于 0
  // status 非空时必须是系统支持的销售状态
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": {
      "id": 1111111,
      "categoryId": 1000016,
      "name": "洽洽经典葵花籽",
      "subtitle": "五折大促",
      "mainImage": "guazi.jpg",
      "subImages": ["guazi.jpg","guazi2.jpg"],
      "detail": "308g-1 袋 五香味",
      "price": 10.2,
      "stock": 10,
      "status": 1,
      "imageHost": "http://file.epoch.fun/mall/"
    }
  }
  // error
  {
    "code": 404,
    "msg": "商品类别不存在 / 已弃用"
  }
  ```

## 商品上下架

- **POST** `/manage/product/shelve.do`
- request

  ```json
  {
    "ids": [1111111,2222222,3333333],
    "status": 1
  }
  // 参数校验规则
  // status 必须是系统支持的销售状态
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "msg": "商品上架 / 下架成功"
  }
  // error
  {
    "code": 500,
    "msg": "商品上架 / 下架失败"
  }
  ```

## 上传商品图片

- **POST** `/manage/product/upload.do`
- request

  ```html
  <form name="upload" action="manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="file"/>
    <input type="submit" value="Upload File"/>
  </form>
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": "5bfd97c4-f8c3-45d5-9264-3f991dc60951.jpg" // 图片上传后的名称
  }
  // error
  {
    "code": 400,
    "msg": "请选择要上传的图片"
  }
  ```

## 上传商品图片 (Simditor)

- 接口说明：集成富文本编辑器 [Simditor][] 图片上传功能
- **POST** `/manage/product/upload_by_simditor.do`
- request
- response

  ```json
  // success
  {
    "success": true,
    "file_path": "http://file.epoch.fun/mall/5bfd97c4-f8c3-45d5-9264-3f991dc60951.jpg"
  }
  // error
  {
    "success": false,
    "msg": "请选择要上传的图片"
  }
  ```

[Simditor]:https://simditor.tower.im/docs/doc-config.html#anchor-upload
