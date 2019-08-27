# API | 后台-商品类别

**接口统一校验规则**

- `999` 未登录
- `403` 无权限 (不是管理员账号)

## 添加商品类别

- **POST** `/manage/category/add.do`
- request

  ```json
  {
    "parentId": 1000000,    // 可选，上级类别 id   默认 0
    "categoryName": "图书"  // 必填，商品类别名称
  }
  // 参数校验规则
  // categoryName 非空
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": 1000001 // 新增商品类别的 id
  }
  // error
  {
    "code": 409,
    "msg": "上级类别中已存在该商品类别名称"

    // "code":404,
    // "msg": "上级类别不存在"
  }
  ```

## 更新商品类别

- **POST** `/manage/category/update.do`
- request

  ```json
  {
    "id": 300002,
    "categoryName": "长裙"
  }
  // 参数校验规则
  // categoryName 非空
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "data": {
      "id": 300002,
      "parentId": 0,
      "name": "长裙",
      "status": 1
    }
  }
  // error
  {
    "code": 409,
    "msg": "上级类别中已存在该商品类别名称"

    // "code": 404,
    // "msg": "商品类别不存在"
  }
  ```

## 启用商品类别

- **POST** `/manage/category/enable.do`
- request

  ```json
  {
    "ids": [1111111,2222222,3333333]
  }
  ```
- response

  ```json
  // success
  {
    "code": 200,
    "msg":"启用成功"
  }
  // error
  {
    "code": 500,
    "msg": "启用失败"
  }
  ```

## 禁用商品类别

- **POST** `/manage/category/disable.do`
- request

  ```json
  {
    "ids": [1111111,2222222,3333333]
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
    "code": 500,
    "msg": "禁用失败"
  }
  ```

## 查询商品类别列表 (平级)

- 接口说明：查询指定类别及其下一级类别
- **GET** `/manage/category/list.do`
- request

  ```json
  {
    "id": 100001 // default 0
  }
  ```
- response

  ```json
  {
    "code": 200,
    "data": {
      "id": 100001,
      "parentId": 0,
      "name": "图书",
      "status": 1,
      "categories": [
        {
          "id": 200000,
          "name": "计算机",
          "parentId": 100001,
          "status": 1
        },
        {
          "id": 200001,
          "name": "小说",
          "parentId": 100001,
          "status": 1
        }
      ]
    }
  }
  ```

## 查询商品类别列表 (递归)

- 接口说明：查询指定类别及其所有递归子类别
- **GET** `/manage/category/list_all.do`
- request

  ```json
  {
    "id": 0 // default 0
  }
  ```
- response

  ```json
  {
    "code": 200,
    "data": {
      "id": 0,
      "name": "全部商品类别",
      "categories": [
        {
          "id": 100001,
          "name": "图书",
          "categories": [
            {
              "id": 200000,
              "name": "计算机",
              "categories":[
                {
                  "id": 300000,
                  "name": "Java",
                  "categories": []
                }
              ]
            },
            {
              "id": 200001,
              "name": "小说",
              "categories": []
            }
          ]
        },
        {
          "id": 100002,
          "name": "服装",
          "categories": [
            {
              "id": 200002,
              "name": "衬衫",
              "categories": []
            },
            {
              "id": 200003,
              "name": "裙子",
              "categories": [
                {
                  "id": 300001,
                  "name": "百褶裙",
                  "categories": []
                },
                {
                  "id": 300002,
                  "name": "A 字裙",
                  "categories": []
                }
              ]
            }
          ]
        }
      ]
    }
  }
  ```
