import { Injectable } from '@angular/core';
import { InMemoryDbService } from 'angular-in-memory-web-api';

@Injectable({
  providedIn: 'root'
})
export class InMemoryDataService implements InMemoryDbService  {

  createDb() {
    let products: Product[];
    products = [
      { id: 1,
        date: '2018-07-01' ,
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
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
      {
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
      {
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
      {
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }

      {
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
{
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
{
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
{
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
{
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
{
        id: 2,
        date: '2018-09-21',
        hs_code: '5673920',
        enterprise: '大同******设备股份有限公司'
        client: 'RANGDON'
        description: 'LED灯泡',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      }
    ];
    return {'products': products};
  }
  constructor() { }
}
