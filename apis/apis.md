# API接口一览
Angular和SpringBoot的接口如下：
 
* 取得表头  (**getHeaders**)
* 查询数据  (**searchData**）
* 导入数据 （**importData**）
* 导出数据 （**exportData**）

## 1. getHeaders
  * *URL:*
    >getHeaders
  * *Request:*
    >no extra parameters
  * *Response:*
    >返回JSON数据，每个元素包含`field`和`title`两个字段
  * *Example*
    * response
    ```javascript
    [{ field: 'id', title: '顺序号' },
    { field: 'date', title: '日期' },
    { field: 'hs_code', title: 'HS编码' },
    { field: 'enterprise', title: '进出口企业' },
    { field: 'client', title: '品牌及客户' }]
    ```