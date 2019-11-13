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
import 'select2';
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
import { ColumnsContainerService } from 'src/app/common/services/columns-container.service';
import { OptionData } from 'select2';
import { DataHeaderDisplayService } from '../../services/data-header-display.service';
import { SaveHeaderDisplaysCallback } from '../../entities/save-header-displays-callback';
import { ExcelReportService } from '../../services/excel-report.service';

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
export class DataSearchComponent implements OnInit, AfterViewChecked, SaveHeaderDisplaysCallback {

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
  public readonly QUERY_CONDITIONS_NUMBER_EACH_ROW = 4;
  public readonly DATA_NUMBER_PER_PAGE = 100;
  public readonly LIST_VALUE_QUERY_NUMBER_PER_PAGE = 100;
  // api urls
  private readonly searchUrl = 'api/searchData';  // URL to web api
  private readonly exportUrl = 'api/downloadFile';  // URL to web api
  private readonly headersUrl = 'api/getHeadersForTable';  // URL to web api
  private readonly getQueryConditionsUrl = 'api/getQueryConditions';  // URL to web api
  private readonly getListValueUrl = 'api/getListValueWithPagination';  // URL to web api

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
  // init height of table(show query conditions with collapse)
  private TABLE_HEIGHT_QUERY_CONDITIONS_SHOWN = 0.60;
  // total height of table(hide query conditions with collapse)
  private TABLE_HEIGHT_QUERY_CONDITIONS_HIDEN = 0.90;
  // is showing query conditions
  private isShowingQueryConditions = true;

  // user access authorities definition
  // 1. export button
  public exportEnabled = true;
  // 2. others


  // data search table's offset
  private dataSearchTableOffset = 0;

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
          // get file name from responose
          const contentDisposition = tempRes.headers._headers.get('content-disposition');
          const attachmentAndFileName: string = contentDisposition[0];
          const fileNameIndex = attachmentAndFileName.indexOf('filename=');
          if (fileNameIndex >= 0) {
            const fileName = attachmentAndFileName.substring(fileNameIndex + 'filename='.length);
            importedSaveAs(res.blob(), fileName);
          } else {
            importedSaveAs(res.blob());
          }
        }
      });
  }

  /**get headers from in memory api */
  getHeaders(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.headersUrl);
  }

  constructor(private http: HttpClient,
    // tslint:disable-next-line: deprecation
    private downloadHttp: Http,
    private commonUtilitiesService: CommonUtilitiesService,
    private currentUserContainer: CurrentUserContainerService,
    public modalService: NgbModal,
    private route: Router,
    public dataSearchConstListService: DataSearchConstListService,
    private dataStatisticsService: DataStatisticsService,
    private columnsContainerService: ColumnsContainerService,
    private dataHeaderDisplayService: DataHeaderDisplayService,
    private excelReportService: ExcelReportService) { }

  // notification for getting header of table
  getHeadersNotification(httpResponse: HttpResponse) {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    }
    // get table's columns
    this.filterColumns(httpResponse.data);
    const allHeaders: Header[] = this.addFormatterToHeaders(httpResponse.data);
    // init data table
    this.initDataTable(allHeaders);
    // init query conditions collapse
    this.initQueryCondtionCollapse();
  }
  // init query condtions collapse
  private initQueryCondtionCollapse() {
    $('#query-conditions-body').on('shown.bs.collapse', this, this.showQueryConditions);
    $('#query-conditions-body').on('hidden.bs.collapse', this, this.hideQueryConditions);
  }
  // show query conditions
  private showQueryConditions(target: any) {
    const that = target.data;
    that.isShowingQueryConditions = true;
    $('#table').bootstrapTable('resetView', {
      // 60 px is for pagination
      height: $(window).height() * that.TABLE_HEIGHT_QUERY_CONDITIONS_SHOWN - 60,
    });
  }
  // hide query conditions
  private hideQueryConditions(target: any) {
    const that = target.data;
    that.isShowingQueryConditions = false;
    $('#table').bootstrapTable('resetView', {
      // 60 px is for pagination
      height: $(window).height() * that.TABLE_HEIGHT_QUERY_CONDITIONS_HIDEN - 60,
    });
  }
  // init data table
  private initDataTable(headers: Header[]): void {
    // if donot destroy table at first, table will not be shown
    $('#table').bootstrapTable('destroy');
    const that: any = this;
    $('#table').bootstrapTable({
      columns: headers,
      method: 'post',
      url: that.searchUrl,
      // 60 px is for pagination
      height: $(window).height() * this.TABLE_HEIGHT_QUERY_CONDITIONS_SHOWN - 60,
      striped: true,
      pageNumber: 1,
      queryParamsType: 'limit',
      pagination: true,
      sortName: '日期',
      sortOrder: 'desc',
      sortable: true,
      queryParams: function (params) {
        return that.getQueryParams(params);
      },
      sidePagination: 'server',
      pageSize: that.DATA_NUMBER_PER_PAGE,
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
    // set data search table offset in advance
    this.dataSearchTableOffset = params.offset;
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
    seq.title = 'No.';
    seq.formatter = function (value, row, index) {
      return that.dataSearchTableOffset + index + 1;
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
    this.commonUtilitiesService.addTooltipFormatter(headers, 100, 150);
    allHeaders = allHeaders.concat(headers);
    // set sortable columns
    headers.forEach(element => {
      if (element.field === '日期') {
        element.sortable = true;
        element.order = 'desc';
      } else {
        element.sortable = false;
      }
    });
    return allHeaders;
  }

  ngOnInit() {
    // get query conditions
    this.getQueryConditions();
    // init access authorities
    this.initAccessAuthorites();
    // load all columns
    this.columnsContainerService.init();
  }
  // set select options to select2
  private setSelectOptions(listQueryCondition: QueryCondition): void {
    const that = this;
    $('#' + listQueryCondition.getUUID()).select2({
      placeholder: this.PLEASE_SELECT + listQueryCondition.getKey(),
      multiple: true,
      closeOnSelect: false,
      ajax: {
        url: this.getListValueUrl,
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        delay: 500, // wait 500 milliseconds before triggering the request
        data: function (params): any {
          const currentPage = params.page || 1;
          const query = {
            queryCondition: listQueryCondition.getKey(),
            term: params.term || '',
            offset: (currentPage - 1) * that.LIST_VALUE_QUERY_NUMBER_PER_PAGE,
            limit: that.LIST_VALUE_QUERY_NUMBER_PER_PAGE,
          };
          return JSON.stringify(query);
        },
        processResults: function (data, params) {
          params.page = params.page || 1;
          return {
            results: data.data.results,
            pagination: {
              more: ((params.page * that.LIST_VALUE_QUERY_NUMBER_PER_PAGE) < data.data.totalCount),
            },
          };
        },
      }
    });
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
    const options = target.data.adjustModalOptions();
    options.size = 'xl';
    const modalRef = service.open(DataDetailComponent, options);
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
  // statistic report
  public onExcelReport(): void {
    // show excel report setting dialog
    this.excelReportService.excelReportSetting();
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
        // from day
        let fromDate = null;
        if (element.getValue()[0] === '' || element.getValue()[0] === undefined) {
          fromDate = new Date();
        } else {
          fromDate = this.commonUtilitiesService.convertDateStringToDate(element.getValue()[0]);
        }
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
          // set to from date
          $(this).datepicker('update', fromDate);
          // set foreground color to black
          $(this).css('color', 'black');
        });
        // to day
        let toDate = null;
        if (element.getValue()[1] === '' || element.getValue()[1] === undefined) {
          toDate = new Date();
        } else {
          toDate = this.commonUtilitiesService.convertDateStringToDate(element.getValue()[1]);
        }
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
          // set to date
          $(this).datepicker('update', toDate);
          // set foreground to black
          $(this).css('color', 'black');
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
        this.setSelectOptions(element);
      }
    });
    return true;
  }
  // resize data table
  private bindResizeDataTableEvent(): void {
    // reset data search table when window's height changes
    let height = this.TABLE_HEIGHT_QUERY_CONDITIONS_SHOWN;
    if (this.isShowingQueryConditions === false) {
      height = this.TABLE_HEIGHT_QUERY_CONDITIONS_HIDEN;
    }
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#table').bootstrapTable('resetView', {
        // 60 px is for pagination
        height: $(window).height() * height - 60,
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
    const selections: OptionData[] = $('#' + queryCondition.getUUID()).select2('data');
    const selectionsArray = [];
    selections.forEach(element => {
      selectionsArray.push(element.text);
    });
    // convert selection from comma to dash
    const selectionsStr = this.commonUtilitiesService.convertArrayCommaSeperatorToDash(selectionsArray);
    const inputQueryCondition = queryCondition.clone();
    inputQueryCondition.setStringValue(selectionsStr);
    queryParams.push(inputQueryCondition);
  }
  // abstract input query condition of amount type or money type
  private abstractInputQueryConditionWithAmountTypeOrMoneyType(
    queryParams: QueryCondition[],
    queryCondition: QueryCondition): void {
    // if nothing is input, then do not put into query params
    // get from value
    let from = this.queryConditionInputModel[queryCondition.getUUID() + '_from'];
    let to = this.queryConditionInputModel[queryCondition.getUUID() + '_to'];
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
  // set model for each query condition
  private setModelForEachQueryCondition(): void {
    this.queryCondtions.forEach(element => {
      this.queryConditionInputModel[element.getUUID()] = '';
    });
  }
  // on set header selections
  public onSetHeaderSelections(): void {
    // append callback
    this.dataHeaderDisplayService.appendSaveEndCallback(this);
    this.dataHeaderDisplayService.getHeadersForSetting();
  }
  // save headers end callback
  public callbackOnEndSaveHeaderDisplays(httpResponse: HttpResponse): void {
    // if save headers end successfully, update headers and data
    if (httpResponse.code === 200) {
      this.getHeaders().subscribe(headersResponse =>
        this.getHeadersNotification(headersResponse));
    }
  }
}
