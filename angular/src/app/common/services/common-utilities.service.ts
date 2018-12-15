import { Injectable } from '@angular/core';
import { Header } from '../entities/header';
import { HttpResponse } from '../entities/http-response';
import { forEach } from '@angular/router/src/utils/collection';
import { element } from '@angular/core/src/render3/instructions';

@Injectable({
  providedIn: 'root'
})
export class CommonUtilitiesService {

  public COMMON_SEPERATOR = '~~';

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
  // deserialize data from httpResponse
  public deserializeDataFromHttpResponse(dictionary: Array<string>, data: any): any {
    // return dictionary variable
    const result = {};
    // lookup all element of input dictionary
    dictionary.forEach(dicElement => {
      data.forEach(dataElement => {
        if (dataElement.key === dicElement) {
          result[dicElement] = dataElement.value;
        }
      });
    });
    return result;
  }
  // serialize data to httpResponse
  public serializeDataToHttpResponse(dictionary: Array<string>, data: any) {
    const result: Array<any> = new Array();
    dictionary.forEach(dicElement => {
      if (data[dicElement] !== undefined) {
        const elementRet: any = {};
        elementRet.key = dicElement;
        elementRet.value = data[dicElement];
        result.push(elementRet);
      }
    });
  }
}
