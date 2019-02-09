import { Component, OnInit, AfterViewChecked, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Http, Headers, RequestOptions, Response, ResponseContentType } from '@angular/http';
import { URLSearchParams } from '@angular/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Product } from '../../entities/product';
import { Header } from '../../../common/entities/header';
import 'jquery';
import 'bootstrap';
import 'bootstrap-datepicker';
import 'bootstrap-table';
import 'bootstrap-select';
import { HttpResponse } from '../../../common/entities/http-response';
import { saveAs as importedSaveAs } from 'file-saver';
import { CommonUtilitiesService } from '../../../common/services/common-utilities.service';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { UserAccessAuthorities } from 'src/app/user-conf/entities/user-access-authorities';
import { NgbModal, NgbModalOptions, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { DataDetailComponent } from '../data-detail/data-detail.component';
import { Router } from '@angular/router';
import { DataSearchConstListService } from '../../services/data-search-const-list.service';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
// json header for download post
const head = new Headers({ 'Content-Type': 'application/json' });
const httpDownloadOptions = {
  headers: head,
  responseType: ResponseContentType.Blob,
};
// json header for download post

@Component({
  selector: 'app-data-search',
  templateUrl: './data-search.component.html',
  styleUrls: ['./data-search.component.css']
})
export class DataSearchComponent implements OnInit, AfterViewChecked, AfterViewInit {

  // sequence field and title
  private static readonly SEQUENCE_FIELD = 'seq';
  private static readonly SEQUENCE_TITLE = '';
  // operations field and title
  private static readonly OPERATIONS_FIELD = 'operations';
  private static readonly OPERATIONS_TITLE = '操作';
  private static readonly DATA_DETAIL_TITLE = '查看详细';
  // api urls
  private searchUrl = 'api/searchData';  // URL to web api
  private exportUrl = 'api/downloadFile';  // URL to web api
  private headersUrl = 'api/getHeaders';  // URL to web api

  // this id is just for compiling pass
  private id: string = null;
  // search parameters
  searchParam = [
    { key: '起始日期', value: '', type: 'Date' },
    { key: '结束日期', value: '', type: 'Date' },
    { key: '加工厂号', value: '', type: 'List' },
    { key: '贸易国', value: '', type: 'List' },
    { key: '申报单位名称', value: '', type: 'List' },
    { key: '货主单位名称', value: '', type: 'List' },
    { key: '经营单位名称', value: '', type: 'List' },
    { key: '主管关区', value: '', type: 'List' },
    { key: '商品名称', value: '', type: 'String' },
  ];
  // ori countries
  public oriCountries: string[] = [];
  // owner companies
  public ownerCompanies: string[] = [];
  // operation companies
  public operationCompanies: string[] = [];
  // apply units
  public applyCompanies: string[] = [];
  // major manage customs
  public majorManageCustoms: string[] = [];
  // manufacturer factory
  public manufacturerFactory = '';
  // product name
  public productName = '';

  // user access authorities definition
  // 1. export button
  public exportEnabled = true;
  // 2. others

  // init access authorities from current user container
  private initAccessAuthorites(): void {
    // get access authorities
    const accessAuthorities: UserAccessAuthorities =
      this.currentUserContainer.getCurrentUserAccessAuthorities();
    // init export button
    if (accessAuthorities['导出数据可否'] === true) {
      this.exportEnabled = true;
    } else {
      this.exportEnabled = false;
    }
  }

  private convertSelection(): void {
    // 1. manufacturer factory
    this.searchParam[2].value =
      this.commonUtilitiesService.convertStringCommaSeperatorToDash(this.manufacturerFactory);
    // 2. ori countries
    this.searchParam[3].value =
      this.commonUtilitiesService.convertArrayCommaSeperatorToDash(this.oriCountries);
    // 3. apply companies
    this.searchParam[4].value =
      this.commonUtilitiesService.convertArrayCommaSeperatorToDash(this.applyCompanies);
    // 4. convert owner companies
    this.searchParam[5].value =
      this.commonUtilitiesService.convertArrayCommaSeperatorToDash(this.ownerCompanies);
    // 5. operation companies
    this.searchParam[6].value =
      this.commonUtilitiesService.convertArrayCommaSeperatorToDash(this.operationCompanies);
    // 6.major manange customs
    this.searchParam[7].value =
      this.commonUtilitiesService.convertArrayCommaSeperatorToDash(this.majorManageCustoms);
    // product name
    this.searchParam[8].value = this.productName;
  }
  // get query conditions
  private getQueryConditions(): any {
    this.convertSelection();
    return this.getQueryTime();
  }

  // download file when exporting data
  onDownloadFile(): void {
    this.downloadFile();
  }
  // data search handler
  onSearch(): void {
    $('#table').bootstrapTable('refresh');
  }

  // download data to file
  async downloadFile(): Promise<void> {
    const queryConditions: any = this.getQueryConditions();
    const searchParamJson = JSON.stringify(queryConditions);
    return this.downloadHttp.post(this.exportUrl, searchParamJson, httpDownloadOptions).toPromise().then(
      res => {
        const tempRes: any = res;
        if (tempRes._body.size === 0) {
          this.currentUserContainer.sessionTimeout();
        } else {
          importedSaveAs(res.blob());
        }
      });
  }

  /**get headers from in memory api */
  getHeaders(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.headersUrl);
  }

  constructor(private http: HttpClient,
    private downloadHttp: Http,
    private commonUtilitiesService: CommonUtilitiesService,
    private currentUserContainer: CurrentUserContainerService,
    public modalService: NgbModal,
    private route: Router,
    public dataSearchConstListService: DataSearchConstListService) {

  }

  getHeadersNotification(httpResponse: HttpResponse) {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    }
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
      url: that.searchUrl,
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
      responseHandler: function (response) {
        if (response.code === 200 && response.data != null) {
          const ret: any = {
            'total': response.data.count,
            'rows': that.commonUtilitiesService.reshapeData(response.data.dataList),
          };
          return ret;
        } else if (response.code === 201) {
          // that.currentUserContainer.sessionTimeout();
          return {
            'rows': [],
            'total': 0
          };
        } else {
          return {
            'rows': [],
            'total': 0,
          };
        }
      },
      onLoadSuccess: function (data) {
        // bind data detail on initial step
        that.bindDataDetailEventHandler(null);
      }
    });
  }
  // get query params
  private getQueryParams(params: any): any {
    const queryConditions = this.getQueryConditions();
    return {
      limit: params.limit,
      offset: params.offset,
      queryConditions: queryConditions,
    };
  }

  // add formatter to all headers
  private addFormatterToHeaders(headers: Header[]) {
    const that: any = this;
    // sequence header
    const seq: Header = new Header(DataSearchComponent.SEQUENCE_FIELD,
      DataSearchComponent.SEQUENCE_TITLE,
      true);
    seq.width = 50;
    seq.formatter = function (value, row, index) {
      return index + 1;
    };

    // operations header
    const operations = new Header(DataSearchComponent.OPERATIONS_FIELD,
      DataSearchComponent.OPERATIONS_TITLE,
      true);
    operations.formatter = function (value, row, index) {
      const buttonId = row.id;
      if (that.hasDataDetailAccessAuthority() === true) {
        return '<button type=\'button\' id=' + buttonId + ' class=\'btn btn-primary btn-xs \'>\
        <span class=\'glyphicon glyphicon-folder-open\'></span>' + '&nbsp;&nbsp;' + DataSearchComponent.DATA_DETAIL_TITLE + '</button>';
      } else {
        return '<button disabled=\'disabled\' type=\'button\' id=' + buttonId + ' class=\'btn btn-primary btn-xs \'>\
        <span class=\'glyphicon glyphicon-folder-open\'></span>' + '&nbsp;&nbsp;' + DataSearchComponent.DATA_DETAIL_TITLE + '</button>';
      }
    };
    operations.width = 100;
    // get all headers
    let allHeaders: Header[] = [seq, operations];
    this.commonUtilitiesService.addTooltipFormatter(headers, 150);
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

    // init access authorities
    this.initAccessAuthorites();
    // bind data detail button on page change event
    $('#table').on('page-change.bs.table', this, this.bindDataDetailEventHandler);
  }

  // just for select picker
  ngAfterViewInit(): void {
    // if call selectpicker in ngOnInit, select control will not be shown for some reason
    // but call selectpicker can resolve this issue in ngAfterViewInit
    this.setSelectOptions('#ori-country');
    this.setSelectOptions('#apply-companies');
    this.setSelectOptions('#owner-companies');
    this.setSelectOptions('#operation-companies');
    this.setSelectOptions('#major-manage-customs');
  }

  private setSelectOptions(id: string): void {
    $(id).selectpicker({
      'liveSearch': true,
    });
    $(id).selectpicker('val', '');
    $(id).selectpicker('refresh');
  }

  // bind data detail event handler to every page
  private bindDataDetailEventHandler(target) {
    if (target == null) {
      // get current page data
      const currentPageData = $('#table').bootstrapTable('getData');
      // bind user edit event, this.modalService is passed as target.data
      for (let i = 0; i < currentPageData.length; i++) {
        const buttonId = '#' + currentPageData[i]['id'];
        $(buttonId).on('click', this, this.showDataDetailModal);
      }
    } else {
      // call from html context
      const component = target.data;
      // get current page data
      const currentPageData = $('#table').bootstrapTable('getData');
      // bind user edit event, this.modalService is passed as target.data
      for (let i = 0; i < currentPageData.length; i++) {
        const buttonId = '#' + currentPageData[i]['id'];
        $(buttonId).on('click', component, component.showUserSettingModal);
      }
    }
  }
  // show data detail modal
  showDataDetailModal(target): void {
    const service: NgbModal = target.data.modalService;
    const data: any = target.data.getCurrentData(this.id);
    // you can not call this.adjustModalOptions,
    // because showUserSettingModal called in html context
    const modalRef = service.open(DataDetailComponent, target.data.adjustModalOptions());
    modalRef.componentInstance.setCurrentData(data);
    modalRef.componentInstance.notifyClose.subscribe(response => target.data.callbackOfShowDataDetailEnd(response));
  }
  // call back when data detail dialog closed
  private callbackOfShowDataDetailEnd(response: any): void {
    // do nothing
  }
  // get current row's data by id
  private getCurrentData(id: any): any {
    const data: any = $('#table').bootstrapTable('getRowByUniqueId', id);
    return data;
  }
  // adjust modal options
  // adjust modal options
  // if don't adjust modal options, modal will not be shown correctly
  adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    return options;
  }
  // get start and end time for querying
  getQueryTime(): any {
    // start time
    if (this.isFontGray('#start-time') === false) {
      const time = $('#start-time').datepicker('getDate');
      this.searchParam[0].value = this.commonUtilitiesService.convertDateToLocalString(time);
    } else {
      this.searchParam[0].value = '';
    }
    // end time
    if (this.isFontGray('#end-time') === false) {
      const time = $('#end-time').datepicker('getDate');
      this.searchParam[1].value = this.commonUtilitiesService.convertDateToLocalString(time);
    } else {
      this.searchParam[1].value = '';
    }
    // copy searchParam to another
    const queryContiditons: any = this.searchParam.slice();
    // date query parameter
    const queryTimeValue = queryContiditons[0].value + '~~' + queryContiditons[1].value;
    const queryTime = { key: '日期', value: queryTimeValue, type: 'Date' };
    queryContiditons.splice(0, 1); // from index=1, delete 1 element
    queryContiditons.splice(0, 1); // from index=1, delete 1 element
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
  // tell if current user has data detail access authority
  private hasDataDetailAccessAuthority(): boolean {
    // get current user's access authorities
    const currentUserAccessAuthorities: UserAccessAuthorities =
      this.currentUserContainer.getCurrentUserAccessAuthorities();
    if (currentUserAccessAuthorities['查看详细可否'] === true) {
      return true;
    } else {
      return false;
    }
  }
  // data statistics
    // show data detail modal
    public onStatistics(): void {
      const service: NgbModal = this.modalService;
      // you can not call this.adjustModalOptions,
      // because showUserSettingModal called in html context
      const modalRef = service.open(DataDetailComponent, this.adjustModalOptions());
    }
}
