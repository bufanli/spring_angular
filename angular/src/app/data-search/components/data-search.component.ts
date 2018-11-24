import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Http, Headers, RequestOptions, Response, ResponseContentType } from '@angular/http';
import { URLSearchParams } from '@angular/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Product } from '../entities/product';
import { Header } from '../entities/header';
import 'jquery';
import 'bootstrap';
import 'bootstrap-datepicker';
import 'bootstrap-table';
import 'bootstrap-select';
import { HeadersResponse } from '../entities/headers-response';
import { saveAs as importedSaveAs } from 'file-saver';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
// json header for download post
const head = new Headers({ 'Content-Type': 'application/json' });
const httpDownloadOptions = {
  headers: head,
  responseType: ResponseContentType.Blob
};
// json header for download post

@Component({
  selector: 'app-data-search',
  templateUrl: './data-search.component.html',
  styleUrls: ['./data-search.component.css']
})
export class DataSearchComponent implements OnInit, AfterViewChecked {
  private searchUrl = 'searchData';  // URL to web api
  private exportUrl = 'downloadFile';  // URL to web api
  private headersUrl = 'getHeaders';  // URL to web api
  // search parameters
  searchParam = [
    { key: '进口关区', value: ''},
    { key: '起始日期', value: '' },
    { key: '结束日期', value: '' },
    { key: '产品名称', value: '' },
    { key: '装货港', value: '' },
    { key: '商品编码', value: '' },
    { key: '规格型号', value: '' }
  ];
  public importCustoms: string[] = [];

  onSearch(): void {
    this.getQueryTime();
    this.convertSelection();
    this.searchData().subscribe(result =>
      this.searchDataNotification(result));
  }

  convertSelection(): void {
    let result = '';
    for (const custom of this.importCustoms) {
      result = result + custom;
      result = result + '||';
    }
    // get rid of last two chars
    result = result.substr(0, result.lastIndexOf('||'));
    this.searchParam[0].value = result;
  }

  // download file when exporting data
  onDownloadFile(): void {
    this.downloadFile();
  }

  // download data to file
  async downloadFile(): Promise<void> {
    this.getQueryTime();
    const searchParamJson = JSON.stringify(this.searchParam);
    return this.downloadHttp.post(this.exportUrl, searchParamJson, httpDownloadOptions).toPromise().then(
      res => {
        importedSaveAs(res.blob());
      });
  }

  /**get headers from in memory api */
  getHeaders(): Observable<HeadersResponse> {
    return this.http.get<HeadersResponse>(this.headersUrl);
  }

  /** search products from the server */
  searchData(): Observable<any> {
    return this.http.post<any>(this.searchUrl, this.searchParam, httpOptions);
  }
  constructor(private http: HttpClient, private downloadHttp: Http) {
  }

  searchDataNotification(result: any) {
    if (result.data == null) {
      $('#table').bootstrapTable('load', []);
    } else {
      $('#table').bootstrapTable('load', this.reshapeData(result.data));
    }
  }

  getHeadersNotification(headersResponse: HeadersResponse) {
    // if donot destroy table at first, table will not be shown
    $('#table').bootstrapTable('destroy');
    // show table's columns
    this.filterColumns(headersResponse.data);
    const allHeaders: Header[] = this.addFormatterToHeaders(headersResponse.data);
    $('#table').bootstrapTable({ columns: allHeaders });
    // show all products after headers are shown
    this.searchData().subscribe(products =>
      this.searchDataNotification(products));
  }
  addFormatterToHeaders(headers: Header[]) {
    const seq: Header = new Header('seq', '', true);
    seq.width = 50;
    seq.formatter = function (value, row, index) {
      return index + 1;
    };
    let allHeaders: Header[] = [seq];
    for (const header of headers) {
      header.class = 'colStyle';
      if (header.title.length > 4) {
        header.width = 200;
      } else {
        header.width = 200;
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
    allHeaders = allHeaders.concat(headers);
    return allHeaders;
  }

  ngOnInit() {
    // init table at first
    $('#table').bootstrapTable({toggle: 'table'});
    // get headers from in memory api
    this.getHeaders().subscribe(headersResponse =>
      this.getHeadersNotification(headersResponse));
    // set date picker's formatter
    $('.input-daterange input').each(function () {
      $(this).datepicker({
        format: 'yyyy/mm/dd',
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
    $('#import-custom').selectpicker('destroy');
    $('#import-custom').selectpicker();
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
  // show tooltip when completing to upload file
  ngAfterViewChecked() {
    $('[data-toggle="tooltip"]').each(function () {
      $(this).tooltip();
    });
  }
}