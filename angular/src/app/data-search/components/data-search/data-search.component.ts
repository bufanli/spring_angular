import { Component, OnInit, AfterViewChecked, AfterViewInit, Query } from '@angular/core';
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
import { DataStatisticsComponent } from '../data-statistics/data-statistics.component';
import { StatisticsReportQueryData } from '../../entities/statistics-report-query-data';
import { ComputeField } from '../../entities/compute-field';
import { ProcessingDialogCallback } from 'src/app/common/interfaces/processing-dialog-callback';
import { DataStatisticsService } from '../../services/data-statistics.service';
import { QueryCondition } from '../../entities/query-condition';
import { UUID } from 'angular2-uuid';
import { QueryConditionRow } from '../../entities/query-conditions-row';
import { UserQueryConditionsComponent } from 'src/app/user-conf/components/user-query-conditions/user-query-conditions.component';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
// json header for download post
// tslint:disable-next-line: deprecation
const head = new Headers({ 'Content-Type': 'application/json' });
const httpDownloadOptions = {
  headers: head,
  // tslint:disable-next-line: deprecation
  responseType: ResponseContentType.Blob,
};
// json header for download post

@Component({
  selector: 'app-data-search',
  templateUrl: './data-search.component.html',
  styleUrls: ['./data-search.component.css']
})
export class DataSearchComponent implements OnInit, AfterViewChecked {

  // sequence field and title
  private static readonly SEQUENCE_FIELD = 'seq';
  private static readonly SEQUENCE_TITLE = '';
  // operations field and title
  private static readonly OPERATIONS_FIELD = 'operations';
  private static readonly OPERATIONS_TITLE = '操作';
  private static readonly DATA_DETAIL_TITLE = '查看详细';
  public readonly TO = '到';
  public readonly PLEASE_INPUT = '请输入';
  public readonly PLEASE_SELECT = '请选择';
  public readonly QUERY_CONDITIONS_NUMBER_EACH_ROW = 3;
  // api urls
  private searchUrl = 'api/searchData';  // URL to web api
  private exportUrl = 'api/downloadFile';  // URL to web api
  private headersUrl = 'api/getHeaders';  // URL to web api
  private getQueryConditionsUrl = 'api/getQueryConditions';  // URL to web api

  // this id is just for compiling pass
  private id: string = null;
  // search parameters
  public queryCondtions: QueryCondition[] = null;
  // query condition input model
  public queryConditionInputModel = {};
  // initialized flag
  private initialized = false;
  // query condition rows
  public queryConditionRows: QueryConditionRow[] = null;

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
    const queryConditions: any = this.abstractInputQueryConditionsIntoParams();
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
    public dataSearchConstListService: DataSearchConstListService,
    private dataStatisticsService: DataStatisticsService) {
  }

  // notification for getting header of table
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
      url: that.searchUrl,
      // 60 px is for pagination
      height: $(window).height() * 0.5 - 60,
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
    const queryConditions = this.abstractInputQueryConditionsIntoParams();
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
    // get query conditions
    this.getQueryConditions();
    // init access authorities
    this.initAccessAuthorites();
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
  // if don't adjust modal options, modal will not be shown correctly
  adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    options.size = 'lg';
    return options;
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
    // init query condition form
    if (!this.initialized) {
      if (!this.initQueryConditionForm()) {
        this.initialized = false;
      } else {
        this.initialized = true;
      }
    }
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
  // statistic report
  public onStatisticsReport(): void {
    // query conditions
    const queryConditions: any = this.abstractInputQueryConditionsIntoParams();
    this.dataStatisticsService.statisticsSetting(queryConditions);
  }
  // get query conditions caller
  private getQueryConditions(): void {
    this.getQueryConditionsImpl().subscribe(
      httpResponse => this.getQueryConditionsNotification(httpResponse));
  }
  // get query conditions implementation
  private getQueryConditionsImpl(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getQueryConditionsUrl);
  }

  // get query conditions notification
  private getQueryConditionsNotification(httpResponse: HttpResponse): void {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    } else {
      // get query condition correctly
      this.queryCondtions = this.convertHttpResponseToQueryConditions
        (httpResponse.data);
      this.setUUIDToQueryConditions();
      // set model for each query condition
      this.setModelForEachQueryCondition();
      // reshape query conditions into rows
      this.reshapeQueryConditionsIntoRows();
      // TODO set it into user container service
    }
  }
  // initial query condition form
  private initQueryConditionForm(): boolean {
    // set date type query conditions
    if (!this.setDateTypeQueryConditions()) {
      return false;
    }
    // set list type query conditions
    if (!this.setListTypeQueryConditions()) {
      return false;
    }
    // resize data table
    this.bindResizeDataTableEvent();
    // bind page change event to data table
    this.bindDataTablePageChangeEvent();
    // get headers from in memory api
    this.getHeaders().subscribe(headersResponse =>
      this.getHeadersNotification(headersResponse));
    // return true if succeesful
    return true;
  }
  // init data table
  private bindDataTablePageChangeEvent(): void {
    $('#table').on('page-change.bs.table', this, this.bindDataDetailEventHandler);
  }
  // set date type query conditions
  private setDateTypeQueryConditions(): boolean {
    if (this.queryCondtions === undefined ||
      this.queryCondtions === null) {
      return false;
    }
    this.queryCondtions.forEach(element => {
      if (element.getType() === 'Date') {
        $('#' + element.getUUID() + '_from').each(function () {
          $(this).datepicker({
            format: 'yyyy/mm/dd',
            autoclose: true,
            todayBtn: 'linked',
            language: 'zh-CN',
            enableOnReadonly: false,
          });
          $(this).datepicker({
            format: 'yyyy/mm/dd',
            autoclose: true,
            todayBtn: 'linked',
            language: 'zh-CN',
            enableOnReadonly: false,
          });
          // set date picker's formatter
          // hook the event handler for gray to black font color
          $(this).datepicker().on('changeDate', function (this) {
            $(this).css('color', 'black');
          });
          // TODO this will be changed to recent one month
          $(this).datepicker('update', new Date());
        });
        $('#' + element.getUUID() + '_to').each(function () {
          $(this).datepicker({
            format: 'yyyy/mm/dd',
            autoclose: true,
            todayBtn: 'linked',
            language: 'zh-CN',
            enableOnReadonly: false,
          });
          $(this).datepicker({
            format: 'yyyy/mm/dd',
            autoclose: true,
            todayBtn: 'linked',
            language: 'zh-CN',
            enableOnReadonly: false,
          });
          // set date picker's formatter
          // hook the event handler for gray to black font color
          $(this).datepicker().on('changeDate', function (this) {
            $(this).css('color', 'black');
          });
          // TODO this will be changed to recent one month
          $(this).datepicker('update', new Date());
        });
      }
    });
    return true;
  }
  // set list type query conditions
  private setListTypeQueryConditions(): boolean {
    if (this.queryCondtions === undefined ||
      this.queryCondtions === null) {
      return false;
    }
    this.queryCondtions.forEach(element => {
      if (element.getType() === 'List') {
        // initialize select options
        this.setSelectOptions('#' + element.getUUID());
      }
    });
    return true;
  }
  // resize data table
  private bindResizeDataTableEvent(): void {
    // reset data search table when window's height changes
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#table').bootstrapTable('resetView', {
        // 60 px is for pagination
        height: $(window).height() * 0.5 - 60,
      });
    });
  }
  // convert http response to query conditions
  private convertHttpResponseToQueryConditions(data: any): QueryCondition[] {
    const queryConditions: QueryCondition[] = [];
    if (data === undefined || data.length === 0) {
      return queryConditions;
    } else {
      data.forEach(element => {
        const queryCondition: QueryCondition =
          new QueryCondition(element.key, element.value, element.type);
        queryConditions.push(queryCondition);
      });
      return queryConditions;
    }
  }
  // set uuid to query conditions
  private setUUIDToQueryConditions(): void {
    if (this.queryCondtions === undefined
      || this.queryCondtions === null) {
      return null;
    }
    this.queryCondtions.forEach(element => {
      element.setUUID(UUID.UUID());
    });
  }
  // reshape query condition into row
  private reshapeQueryConditionsIntoRows(): void {
    // rows amount
    let rowsNumber = Math.floor(this.queryCondtions.length / this.QUERY_CONDITIONS_NUMBER_EACH_ROW);
    const extra = this.queryCondtions.length % this.QUERY_CONDITIONS_NUMBER_EACH_ROW;
    if (extra > 0) {
      rowsNumber = rowsNumber + 1;
    }
    // prepare query condition row
    this.queryConditionRows = [];
    for (let i = 0; i < rowsNumber; i++) {
      this.queryConditionRows.push(new QueryConditionRow());
    }
    // put every query condition into ervery fix row
    for (let i = 0; i < this.queryCondtions.length; i++) {
      const row = Math.floor(i / this.QUERY_CONDITIONS_NUMBER_EACH_ROW);
      this.queryConditionRows[row].pushQueryCondition(this.queryCondtions[i]);
    }
  }
  // abstract input query condition into query condition parameters
  private abstractInputQueryConditionsIntoParams(): QueryCondition[] {
    const queryParams: QueryCondition[] = [];
    this.queryCondtions.forEach(element => {
      if (element.getType() === 'Date') {
        this.abstractInputQueryConditionWithDateType(queryParams, element);
      } else if (element.getType() === 'String') {
        this.abstractInputQueryConditionWithStringType(queryParams, element);
      } else if (element.getType() === 'List') {
        this.abstractInputQueryConditionWithListType(queryParams, element);
      } else if (element.getType() === 'Amount' || element.getType() === 'Money') {
        this.abstractInputQueryConditionWithAmountTypeOrMoneyType(queryParams, element);
      } else {
        // it is impossible here
      }
    });
    return queryParams;
  }
  // abstract date type input query condition
  private abstractInputQueryConditionWithDateType(
    queryParams: QueryCondition[],
    queryCondition: QueryCondition): void {
    const inputQueryCondition = queryCondition.clone();
    // get start time
    const startTimeID = '#' + queryCondition.getUUID() + '_from';
    const startTime = $(startTimeID).datepicker('getDate');
    const startTimeStr = this.commonUtilitiesService.convertDateToLocalString(startTime);
    // get end time
    const endTimeID = '#' + queryCondition.getUUID() + '_to';
    const endTime = $(endTimeID).datepicker('getDate');
    const endTimeStr = this.commonUtilitiesService.convertDateToLocalString(endTime);
    // concatenate start time and end time into one time string
    const timeStr = startTimeStr + '~~' + endTimeStr;
    inputQueryCondition.setStringValue(timeStr);
    // push input query condition into query params
    queryParams.push(inputQueryCondition);
  }
  // abstract string type input query condition
  private abstractInputQueryConditionWithStringType(
    queryParams: QueryCondition[],
    queryCondition: QueryCondition): void {
    // if no text input, then nothing is input into query params
    const queryString = this.queryConditionInputModel[queryCondition.getUUID()];
    if (queryString === undefined || queryString === '') {
      return;
    } else {
      const inputQueryCondition = queryCondition.clone();
      inputQueryCondition.setStringValue(queryString);
      // push input query condition into query params
      queryParams.push(inputQueryCondition);
    }
  }
  // abstract input query condition of list type
  private abstractInputQueryConditionWithListType(
    queryParams: QueryCondition[],
    queryCondition: QueryCondition): void {
    // if nothing is selected, then do not put into query params
    let selections = this.queryConditionInputModel[queryCondition.getUUID()];
    if (selections === '') {
      return;
    } else {
      // convert selection from comma to dash
      selections = this.commonUtilitiesService.convertArrayCommaSeperatorToDash(selections);
      const inputQueryCondition = queryCondition.clone();
      inputQueryCondition.setStringValue(selections);
    }
  }
  // abstract input query condition of amount type or money type
  private abstractInputQueryConditionWithAmountTypeOrMoneyType(
    queryParams: QueryCondition[],
    queryCondition: QueryCondition): void {
    // if nothing is input, then do not put into query params
    // get from value
    let from = this.queryConditionInputModel[queryCondition.getUUID() + '_from'];
    let to = this.queryConditionInputModel[queryCondition.getUUID() + '_to'];
    if (from === '' && to === '') {
      return;
    } else {
      // if type is amount, then get integer only
      if (queryCondition.getType() === 'Amount') {
        if (from !== '') {
          from = Math.floor(from);
        }
        if (to !== '') {
          to = Math.floor(to);
        }
      }
      // contenate from and to with dash
      const value = from + '~~' + to;
      const inputQueryCondition = queryCondition.clone();
      inputQueryCondition.setStringValue(value);
      queryParams.push(inputQueryCondition);
    }
  }
  // set model for each query condition
  private setModelForEachQueryCondition(): void {
    this.queryCondtions.forEach(element => {
      this.queryConditionInputModel[element.getUUID()] = '';
    });
  }
}
