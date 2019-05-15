import { Injectable } from '@angular/core';
import { InMemoryDbService } from 'angular-in-memory-web-api';
import { Product } from '../../data-search/entities/product';
import { Header } from '../entities/header';
import { UserBasicInfo } from '../../user-conf/entities/user-basic-info';
@Injectable({
  providedIn: 'root'
})
export class InMemoryDataService implements InMemoryDbService {

  createDb() {
    let products: Product[];
    products = [
      {
        id: 1,
        date: '2018-07-01',
        hs_code: '85451100',
        enterprise: '******科技股份有限公司',
        client: 'RANGDONG',
        description: 'LED工矿灯配件（散件）',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      },
      {
        id: 2,
        date: '2018-09-01',
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
      {
        id: 2,
        date: '2018-09-02',
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
      {
        id: 2,
        date: '2018-09-04',
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
      {
        id: 2,
        date: '2018-09-05',
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
      {
        id: 2,
        date: '2018-09-21',
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
      {
        id: 2,
        date: '2018-09-21',
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
      {
        id: 2,
        date: '2018-09-21',
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
      {
        id: 2,
        date: '2018-09-21',
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
      {
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司',
        client: 'RANGDON',
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
    ];
    let headers: Header[];
    headers = [
      { field: 'id', title: '顺序号', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true, order: 'dsc' },
      { field: 'date', title: '日期', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
      { field: 'hs_code', title: 'HS编码', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
      { field: 'enterprise', title: '进出口企业', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
      { field: 'client', title: '品牌及客户', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
      { field: 'description', title: '产品描述', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
      { field: 'country', title: '国家', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
      { field: 'unit_price', title: '申请单价', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
      { field: 'total_price', title: '申请总价', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
      { field: 'amount', title: '数量', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
      { field: 'amount_unit', title: '数量单位', visible: true, formatter: null, class: 'colStyle', width: 100, sortable: true , order: 'dsc' },
    ];
    const headersResponse = {
      code: '200',
      message: '数据表头成功',
      data: headers,
    };
    // setup users list
    let users: UserBasicInfo[];
    users = [
      {
        'userID': 'wechat0001', '昵称': '沧海笑', '名字': '李大宝', '国家': '中国',
        '地址': '江苏南京', '城市': '南京', '密码': '123456', '年龄': '23', '性别': '男',
        '电子邮件': 'dabao@163.com', '电话号码': '13423456719', '省份': '江苏'
      },
    ];
    return {
      'products': products, 'search': products.slice(0, 5), 'getHeaders': headersResponse,
      'getUsers': users
    };
  }
  constructor() {
  }
}
