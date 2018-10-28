import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { URLSearchParams } from '@angular/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Product } from './data-entry/product';
import { Header } from './data-entry/header';
import 'jquery';
import 'bootstrap';
import 'bootstrap-datepicker';
import 'bootstrap-table';
import 'bootstrap-select';
import { HeadersResponse } from './data-entry/headers-response';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Component({
  selector: 'app-data-search',
  templateUrl: './data-search.component.html',
  styleUrls: ['./data-search.component.css']
})
export class DataSearchComponent implements OnInit {
  private searchUrl = 'searchData';  // URL to web api
  private exportUrl = 'downloadFile';  // URL to web api
  private headersUrl = 'getHeaders';  // URL to web api
  // search parameters
  searchParam = [
    { key: '海关编码', value: '' },
    { key: '起始日期', value: '' },
    { key: '结束日期', value: '' },
    { key: '商品名称', value: '' },
    { key: '贸易方式', value: '' },
    { key: '企业名称', value: '' },
    { key: '收发货地', value: '' }
  ];

  onSearch(): void {
    this.getQueryTime();
    this.searchData().subscribe(result =>
      this.searchDataNotification(result));
  }

  onDownloadFile(): void {
    this.downloadFile().subscribe(data => this.exportNotification(data));
  }

  // download data to file
  downloadFile(): Observable<any> {
    this.getQueryTime();
    // this.http.post(this.exportUrl, this.searchParam, httpOptions);
    return this.http.post<any>(this.exportUrl, this.searchParam, httpOptions);
  }

  /**get headers from in memory api */
  getHeaders(): Observable<HeadersResponse> {
    return this.http.get<HeadersResponse>(this.headersUrl);
  }

  /** search products from the server */
  searchData(): Observable<any> {
    return this.http.post<any>(this.searchUrl, this.searchParam, httpOptions);
  }
  constructor(private http: HttpClient) {
  }

  searchDataNotification(result: any) {
    if (result.data == null) {
      $('#table').bootstrapTable('load', []);
    } else {
      $('#table').bootstrapTable('load', this.reshapeData(result.data));
    }
  }
  exportNotification(result: any) {
    alert('export ok');
  }
  getHeadersNotification(headersResponse: HeadersResponse) {
    // if donot destroy table at first, table will not be shown
    $('#table').bootstrapTable('destroy');
    // show table's columns
    this.filterColumns(headersResponse.data);
    $('#table').bootstrapTable({ columns: headersResponse.data });
    // show all products after headers are shown
    this.searchData().subscribe(products =>
      this.searchDataNotification(products));
  }

  ngOnInit() {
    // get headers from in memory api
    this.getHeaders().subscribe(headersResponse =>
      this.getHeadersNotification(headersResponse));

    // set date picker's formatter
    $('.input-daterange input').each(function () {
      $(this).datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: 'linked',
        language: 'zh-CN',
      });
      // hook the event handler for gray to black font color
      $(this).datepicker().on('changeDate', function (this) {
        $(this).css('color', 'black');
      });
    });

    // set initial date to datepicker
    $('.input-daterange input').each(function () {
      $(this).datepicker('update', new Date());
    });

    // selectpicker must be called, otherwise select will not be shown
    $('#trade-mode').selectpicker('destroy');
    $('#trade-mode').selectpicker();
  }
  // get start and end time for querying
  getQueryTime() {
    // start time
    if (this.isFontGray('#start-time') === false) {
      const time = $('#start-time').datepicker('getDate').toLocaleString();
      this.searchParam[1].value = time.slice(0, 10);
    } else {
      this.searchParam[1].value = '';
    }
    // end time
    if (this.isFontGray('#end-time') === false) {
      const time = $('#end-time').datepicker('getDate').toLocaleString();
      this.searchParam[2].value = time.slice(0, 10);
    } else {
      this.searchParam[2].value = '';
    }
  }
  // tell whether the font color is gray or not
  isFontGray(id: string): boolean {
    if ($(id).css('color') === 'rgb(128, 128, 128)') {
      return true;
    } else {
      return false;
    }
  }
  // set visible to false for some hidden columns
  filterColumns(headers: Header[]) {
    for (const header of headers) {
      if (header.field === 'id') {
        header.visible = false;
      }
    }
  }
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
}
