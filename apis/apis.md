# API接口一览
Angular和SpringBoot的接口如下：
 
* 取得表头  (**getHeaders**)
* 查询数据  (**searchData**）
* 导入数据 （**uploadFiles**）
* 导出数据 （**exportData**）

## 1. getHeaders
  * *URL:*
    >getHeaders[GET]
  * *Request:*
    >no extra parameters
  * *Response:*
    >返回JSON数组，每个元素包含`field`和`title`两个字段,field和title相同，并且支持中文命名
  * *Example*
    * response
    ```javascript
    // 查询成功
    {
      code: 200,
      message: '查询表头成功'
      data:[
        { field: '顺序号', title: '顺序号' },
        { field: '日期', title: '日期' },
        { field: 'HS编码', title: 'HS编码' },
        { field: '进出口企业', title: '进出口企业' },
        { field: '品牌及客户', title: '品牌及客户' }
      ]
    }
    // 查询失败
    {
      code: 201,
      message: '查询表头失败',
    }
    ```
## 2. searchData
 * *URL:*
   >searchData[POST]
 * *Request[任意]:*
   >查询条件的JSON数组，每个元素包含`key`和`value`两个字段
   * 如果不存在查询条件，那么全表查询
 * *Response:*
   >查询结果的JSON数组，如果对于某条数据，该属性不存在，
   那么可以不填充该属性，Angular侧将自动表示成`-`
   * 所有出现的属性必须在 `getHeaders` API中的field中出现
 * *Examples*
   * request
   ```javascript
   [
      { key: 'country', value: '中国' },
      { key: 'client', value: 'RAN' }
   ];
   ```
   * response
    ```javascript
    // 查询数据成功
    {
      code: 200,
      message: '数据查询成功',
      data:[
        {
          id: 2,
          date: '2018-09-02',
          hs_code: '5673920',
          enterprise: '大同******设备股份有限公司',
          client: 'RANGDON',
          description: 'LED灯泡',
          country: '越南',
          unit_price: 4.0927,
          total_price: 53205.00 ,
          amount: 13000,
          amount_unit: '套',
        },
        {
          id: 2,
          date: '2018-09-03',
          hs_code: '5673920',
          enterprise: '大同******设备股份有限公司',
          client: 'RANGDON',
          description: 'LED灯泡',
          country: '越南',
          unit_price: 4.0927,
          total_price: 53205.00,
          amount: 13000,
          amount_unit: '套',
        },
      ]
    }
    // 查询数据失败
    {
      code: 201,
      message: '数据查询失败原因'
    } 
     ```

## 3. uploadFiles
 * *URL:*
  >uploadFiles[Post]
 * *Request:*
  >file MultipartFile[]
 * *Response:*
  >如果成功，返回code = 200, 否则，返回code其他
 * *Example:*
   ```javascript
   * response
   // 上传成功
   {
     code: 200,
     message: 文件上传成功,
     details:[
       "文件名1","文件名2"
     ]
   }
   // 上传失败
   {
     code: 201,
     message: %number%个文件导入成功,%number%个文件导入失败。
     details:[
       {
         successed:[
           "文件名1",
           "文件名2",
         ],
         failed:[
           {
             fileName:"文件名",
             failedReason:"失败原因"
           },
           {
             fileName:"文件名",
             failedReason:"失败原因"
           }
         ]
     ]
   }
   ```