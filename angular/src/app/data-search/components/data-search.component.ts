import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Http, Headers, RequestOptions, Response, ResponseContentType } from '@angular/http';
import { URLSearchParams } from '@angular/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Product } from '../entities/product';
import { Header } from '../../common/entities/header';
import 'jquery';
import 'bootstrap';
import 'bootstrap-datepicker';
import 'bootstrap-table';
import 'bootstrap-select';
import { HttpResponse } from '../../common/entities/http-response';
import { saveAs as importedSaveAs } from 'file-saver';
import { CommonUtilitiesService } from '../../common/services/common-utilities.service';

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
    { key: '进口关区', value: '', type: 'List' },
    { key: '起始日期', value: '', type: 'Date' },
    { key: '结束日期', value: '', type: 'Date' },
    { key: '产品名称', value: '', type: 'String' },
    { key: '装货港', value: '', type: 'String' },
    { key: '商品编码', value: '', type: 'String' },
    { key: '规格型号', value: '', type: 'String' }
  ];
  public importCustoms: string[] = [];

  convertSelection(): void {
    let result = '';
    for (const custom of this.importCustoms) {
      result = result + custom;
      result = result + '~~';
    }
    // get rid of last two chars
    result = result.substr(0, result.lastIndexOf('~~'));
    // convert empty to dash
    result = this.commonUtilitiesService.convertEmptyToDash(result);
    // set to params
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
  getHeaders(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.headersUrl);
  }

  constructor(private http: HttpClient,
    private downloadHttp: Http,
    private commonUtilitiesService: CommonUtilitiesService) {
  }

  getHeadersNotification(httpResponse: HttpResponse) {
    // if donot destroy table at first, table will not be shown
    $('#table').bootstrapTable('destroy');
    // get table's columns
    this.filterColumns(httpResponse.data);
    const allHeaders: Header[] = this.addFormatterToHeaders(httpResponse.data);
    // init data table
    this.initDataTable(allHeaders);
  }

  // init data table
  private initDataTable(headers: Header[]): void {
    const that: any = this;
    $('#table').bootstrapTable({
      columns: headers,
      method: 'post',
      // contentType: 'application/x-www-form-urlencoded',
      url: '/searchData',
      striped: true,
      pageNumber: 1,
      queryParamsType: 'limit',
      pagination: true,
      queryParams: function (params) {
        return that.getQueryParams(params);
      },
      sidePagination: 'server',
      pageSize: 10,
      pageList: [],
      // showColumns: true,
      locale: 'zh-CN',
      dataField: 'res',
      responseHandler: function (response) {
        that.passDataSearchResultToTable(response);
      }
    });
  }
  // get query params
  private getQueryParams(params: any): any {
    this.convertSelection();
    const queryConditions = this.getQueryTime();
    return {
      limit: params.limit,
      offset: params.offset,
      queryConditions: queryConditions,
    };
  }
  // dispose data search response
  private passDataSearchResultToTable(response: any): any {
    if (response) {
      return {
        'rows': response.list,
        'total': response.total
      };
    } else {
      return {
        'rows': [],
        'total': 0
      };
    }
  }
  // add formatter to all headers
  private addFormatterToHeaders(headers: Header[]) {
    const seq: Header = new Header('seq', '', true);
    seq.width = 50;
    seq.formatter = function (value, row, index) {
      return index + 1;
    };
    let allHeaders: Header[] = [seq];
    this.commonUtilitiesService.addTooltipFormatter(headers, 200);
    allHeaders = allHeaders.concat(headers);
    return allHeaders;
  }

  ngOnInit() {
    // init table at first
    $('#table').bootstrapTable({ toggle: 'table' });
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
        enableOnReadonly: false,
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
      const time = $('#start-time').datepicker('getDate');
      this.searchParam[1].value = this.commonUtilitiesService.convertDateToLocalString(time);
    } else {
      this.searchParam[1].value = '';
    }
    // end time
    if (this.isFontGray('#end-time') === false) {
      const time = $('#end-time').datepicker('getDate');
      this.searchParam[2].value = this.commonUtilitiesService.convertDateToLocalString(time);
    } else {
      this.searchParam[2].value = '';
    }
    // copy searchParam to another
    const queryContiditons: any = this.searchParam.slice();
    // date query parameter
    const queryTimeValue = queryContiditons[1].value + '~~' + queryContiditons[2].value;
    const queryTime = { key: '日期', value: queryTimeValue, type: 'Date' };
    queryContiditons.splice(1, 1); // from index=1, delete 1 element
    queryContiditons.splice(1, 1); // from index=1, delete 1 element
    queryContiditons.push(queryTime);
    return queryContiditons;
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

  // show tooltip when completing to upload file
  ngAfterViewChecked() {
    $('[data-toggle="tooltip"]').each(function () {
      $(this).tooltip();
    });
  }
}
