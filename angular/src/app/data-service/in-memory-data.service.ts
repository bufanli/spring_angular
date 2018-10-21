import { Injectable } from '@angular/core';
import { InMemoryDbService } from 'angular-in-memory-web-api';
import { Product } from '../data-search/data-entry/product';
import { Header} from '../data-search/data-entry/header';
import { HeadersResponse } from '../data-search/data-entry/headers-response';

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
      { field: 'id', title: '顺序号' },
      { field: 'date', title: '日期' },
      { field: 'hs_code', title: 'HS编码' },
      { field: 'enterprise', title: '进出口企业' },
      { field: 'client', title: '品牌及客户' },
      { field: 'description', title: '产品描述' },
      { field: 'country', title: '国家' },
      { field: 'unit_price', title: '申请单价' },
      { field: 'total_price', title: '申请总价' },
      { field: 'amount', title: '数量' },
      { field: 'amount_unit', title: '数量单位' },
    ];
    const headersResponse = {
      code: '200',
      message: '数据表头成功',
      headers: headers,
    };
    return { 'products': products, 'search': products.slice(0, 5), 'getHeaders': headersResponse };
  }
  constructor() { }
}
