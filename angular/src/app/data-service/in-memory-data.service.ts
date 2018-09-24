import { Injectable } from '@angular/core';
import { InMemoryDbService } from 'angular-in-memory-web-api';

@Injectable({
  providedIn: 'root'
})
export class InMemoryDataService implements InMemoryDbService  {

  createDb() {
    const  products = [
      { seq: 1,
        date: '2018-07-01' ,
        hs_code: '85451100',
        enterprise: '方大炭素新材料科技股份有限公司',
        client: 'RANGDONG',
        description: 'LED工矿灯配件（散件）',
        country: '越南',
        unit_price: 4.0927,
        total_price: 53205.00,
        amount: 13000,
        amount_unit: '套',
      },
    ];
    return {'products': products};
  }
  constructor() { }
}
