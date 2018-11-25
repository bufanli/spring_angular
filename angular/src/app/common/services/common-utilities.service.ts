import { Injectable } from '@angular/core';
import { Header } from '../entities/header';

@Injectable({
  providedIn: 'root'
})
export class CommonUtilitiesService {

  constructor() { }

  // reshape data result
  // input
  // [
  // {keyvalue:{code:11,message:22}},
  // {keyvalue:{code:33,message:44}},
  // ]
  // output
  // [{code:11, message:22},
  // {code:33, message:44}
  // ]
  reshapeData(data: any) {
    const result: any[] = [];
    let index = 0;
    for (const row of data) {
      result[index] = row.keyValue;
      index++;
    }
    return result;
  }

  // add tooltip formatter to table
  addTooltipFormatter(headers: Header[], width) {
    for (const header of headers) {
      header.class = 'colStyle';
      if (header.title.length > 4) {
        header.width = width;
      } else {
        header.width = width;
      }
      header.formatter = function (value, row, index) {
        value = value ? value : '';
        let length = value.length;
        if (length && length > 12) {
          length = 12;
          return '<span class = "text-primary" data-toggle = "tooltip" \
                  title = ' + value + '>' + value.substring(0, length) + '...' + '</span>';
        } else {
          return value;
        }
      };
    }
  }
}
