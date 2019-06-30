import { Injectable } from '@angular/core';
import { Header } from '../entities/header';
import { HttpResponse } from '../entities/http-response';
import { forEach } from '@angular/router/src/utils/collection';
import { element } from '@angular/core/src/render3/instructions';
import { NgbModal, NgbModalOptions, NgbModalConfig, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CommonDialogCallback } from '../interfaces/common-dialog-callback';
import { ModalDialogComponent } from '../components/modal-dialog/modal-dialog.component';
import { ProcessingDialogCallback } from '../interfaces/processing-dialog-callback';

@Injectable({
  providedIn: 'root'
})
export class CommonUtilitiesService {

  public DATA_COMMON_SEPERATOR = '~~';
  public VIEW_COMMON_SEPERATOR = ',';
  public PROCESSING_TITLE = '处理中';
  public PROCESSING_BODY = '处理中,请稍后';
  public CHINA_PROVINCES: string[] = [
    '北京',
    '天津',
    '河北',
    '山西',
    '内蒙古',
    '辽宁',
    '吉林',
    '黑龙江',
    '上海',
    '江苏',
    '浙江',
    '安徽',
    '福建',
    '江西',
    '山东',
    '河南',
    '湖北',
    '湖南',
    '广东',
    '广西',
    '海南',
    '重庆',
    '四川',
    '贵州',
    '云南',
    '西藏',
    '陕西',
    '甘肃省',
    '青海',
    '宁夏',
    '新疆',
    '台湾',
    '香港特别行政区',
    '澳门',
  ];
  // system reserved users
  private readonly ADMIN_USER = 'sinoshuju_admin';
  private readonly DEFAULT_USER = 'sinoshuju_default';
  // processing modal dialog handler
  private processingDialog: NgbModalRef = null;

  constructor(public modalService: NgbModal) { }

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
  addTooltipFormatter(headers: Header[], smallWidth, bigWidth) {
    for (const header of headers) {
      header.class = 'colStyle';
      if (header.title.length > 4) {
        header.width = bigWidth;
      } else {
        header.width = smallWidth;
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
          // convert string boolean to boolean
          if (dataElement.value === 'true' || dataElement.value === 'TRUE') {
            result[dicElement] = true;
          } else if (dataElement.value === 'false' || dataElement.value === 'FALSE') {
            result[dicElement] = false;
          } else {
            result[dicElement] = dataElement.value;
          }
        }
      });
    });
    return result;
  }
  // serialize data to httpResponse
  public serializeDataToHttpResponse(dictionary: Array<string>, data: any): Array<any> {
    const result: Array<any> = new Array();
    dictionary.forEach(dicElement => {
      if (data[dicElement] !== undefined) {
        const elementRet: any = {};
        elementRet.key = dicElement;
        let elementValue: any = null;
        // convert boolean to string boolean
        if (data[dicElement] === true) {
          elementValue = 'TRUE';
        } else if (data[dicElement] === false) {
          elementValue = 'FALSE';
        } else {
          // if it is not a boolean, then copy it as is
          elementValue = data[dicElement];
        }
        elementRet.value = elementValue;
        result.push(elementRet);
      }
    });
    return result;
  }
  // convert date to local string 2018/12/12
  convertDateToLocalString(date: any): string {
    if (date == null) {
      return '';
    } else {
      let timeStr: string = date.toLocaleString();
      // get rid of all chars until last space
      const lastSpacePosition = timeStr.indexOf(' ');
      timeStr = timeStr.substring(0, lastSpacePosition);
      return timeStr.slice(0, 10).trim();
    }
  }
  // show common dialog
  showCommonDialog(dialogTitle: string,
    dialogBody: string,
    dialogType: string,
    callback: CommonDialogCallback,
    sourceID: string): void {
    // you can not call this.adjustModalOptions,
    // because showUserSettingModal called in html context
    const modalRef = this.modalService.open(ModalDialogComponent, this.adjustModalOptions());
    modalRef.componentInstance.setTitle(dialogTitle);
    modalRef.componentInstance.setBody(dialogBody);
    modalRef.componentInstance.setSourceID(sourceID);
    modalRef.componentInstance.setType(dialogType);
    modalRef.componentInstance.notifier.subscribe(response => this.callbackOfModalDialog(response, callback));
  }
  // show simple dialog
  showSimpleDialog(dialogTitle: string,
    dialogBody: string,
    dialogType: string): void {
    // you can not call this.adjustModalOptions,
    // because showUserSettingModal called in html context
    const modalRef = this.modalService.open(ModalDialogComponent, this.adjustModalOptions());
    modalRef.componentInstance.setTitle(dialogTitle);
    modalRef.componentInstance.setBody(dialogBody);
    modalRef.componentInstance.setSourceID(null);
    modalRef.componentInstance.setType(dialogType);
  }
  // show processing dialog
  showProcessingDialog(callback: ProcessingDialogCallback, data: any, sourceID: string) {
    // show processing dialog
    const modalRef = this.modalService.open(ModalDialogComponent, this.adjustModalOptions());
    modalRef.componentInstance.setTitle(this.PROCESSING_TITLE);
    modalRef.componentInstance.setBody(this.PROCESSING_BODY);
    modalRef.componentInstance.setType('processing');
    this.processingDialog = modalRef;
    // call callback to process
    callback.callbackOnProcessing(sourceID, data);
  }
  // close processing dialog
  // it should be called on process finishing
  closeProcessingDialog() {
    this.processingDialog.close();
    this.processingDialog = null;
  }

  // callback of modal dialog
  private callbackOfModalDialog(response: any, callback: CommonDialogCallback) {
    callback.callbackOnConfirm(response);
  }

  // adjust show dialog options
  private adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    return options;
  }
  // convert empty string to ~~
  public convertEmptyToDash(str: string): string {
    if (str === '') {
      return '~~';
    } else {
      return str;
    }
  }
  // convert comma seperator to dash seperator(string)
  // AA,BB -> AA~~BB
  convertStringCommaSeperatorToDash(srcString: string): string {
    const strArr = srcString.split(',');
    let temp = '';
    for (const entry of strArr) {
      if (entry !== '') {
        temp += entry;
        temp += '~~';
      }
    }
    if (temp === '') {
      temp = '~~';
    }
    return temp;
  }
  // covert comman seperator to dash
  // ['AA','BB'] -> AA~~BB
  convertArrayCommaSeperatorToDash(srcArr: string[]): string {
    let result = '';
    for (const entry of srcArr) {
      result = result + entry;
      result = result + '~~';
    }
    // if it is empty, add ~~
    if (result === '') {
      result = '~~';
    }
    return result;
  }
  ellipsis(src: string, length: number): string {
    let len = src.length;
    if (len && len > length) {
      len = length;
      return src.substring(0, len) + '...';
    } else {
      return src;
    }
  }
  // convert date string to date
  // for example, 2019/01/01 to date
  public convertDateStringToDate(dateString: string): Date {
    // get year of date string
    const year = Number.parseInt(dateString.substring(0, 4));
    // get month of date string
    const month = Number.parseInt(dateString.substring(5, 7));
    // get day of date string
    const day = Number.parseInt(dateString.substring(8, 10));
    // return date
    const ret = new Date();
    // month is based from 0 to 11
    ret.setUTCFullYear(year, month - 1, day);
    return ret;
  }
  // init select picker
  public setSelectOptions(id: string, liveSearch: boolean): void {
    $(id).selectpicker({
      'liveSearch': liveSearch,
    });
    $(id).selectpicker('val', '');
    $(id).selectpicker('refresh');
  }
  // tell whether it is system reserved user or not
  public isSystemReservedUser(userName: string): boolean {
    if (userName === this.ADMIN_USER ||
      userName === this.DEFAULT_USER) {
      return true;
    } else {
      return false;
    }
  }
}
