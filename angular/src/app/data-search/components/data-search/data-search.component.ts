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

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
// json header for download post
const head = new Headers({ 'Content-Type': 'application/json' });
const httpDownloadOptions = {
  headers: head,
  responseType: ResponseContentType.Json,
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

  public IMPORT_CUSTOMS_ENUM = [
    '天津关区',
    '上海关区',
    '深圳关区',
    '广东分署',
    '大连关区',
    '青岛关区',
    '宁波关区',
    '厦门关区',
    '许昌海关',
    '合肥关区',
    '南京关区',
    '郑州关区',
    '兰州海关',
    '杭州关区',
    '福州关区',
    '黄埔关区',
    '北京关区',
    '南宁关区',
    '重庆关区',
  ];
  public SHIP_PORTS = [
    '巴伦西亚',
    '圣多斯',
    '温哥华',
    '巴西',
    '墨尔本',
    '新加坡',
    '布宜诺斯艾利斯',
    '布里斯班',
    '香港',
    '帕腊纳瓜',
    '蒙得维的亚',
    '釜山',
    '陶朗加',
    '高雄',
    '萨拉特',
    '丹吉尔',
    '安特卫普',
    '悉尼',
    '巴生港',
    '伊塔曰阿伊',
    '沙特阿拉伯',
    '罗萨里奥',
    '曼萨尼略',
    '利特尔顿港',
    '热那亚',
    '中国境内',
    '洛杉矶',
    '意大利',
    '德班',
    '长滩',
    '乌拉圭',
    '内皮尔',
    '里约格兰德',
    '弗里曼特尔',
    '南非(阿扎尼亚)',
    '新西兰',
    '马来西亚',
    '米纳阿卜杜拉',
    '鹿特丹',
    '澳大利亚',
    '利尔肯',
    '塔科马',
    '洛美',
    '摩洛哥',
    '卡塔尔',
    '里约热内卢',
    '美国',
    '槟城',
    '不来梅港',
    '奥克兰',
    '柔弗巴鲁',
    '阿耳黑西腊斯',
    '迪拜',
    '台湾省',
    '都柏林',
    '瓦尔帕来索',
    '加拿大',
    '不来梅',
    '伊斯坦布尔',
    '阿根廷',
    '巴尔博亚',
    '重庆关区',
  ];
  // this id is just for compiling pass
  private id: string = null;
  // search parameters
  searchParam = [
    { key: '进口关区', value: '', type: 'List' },
    { key: '起始日期', value: '', type: 'Date' },
    { key: '结束日期', value: '', type: 'Date' },
    { key: '产品名称', value: '', type: 'String' },
    { key: '装货港', value: '', type: 'List' },
    { key: '商品编码', value: '', type: 'List' },
    { key: '规格型号', value: '', type: 'String' }
  ];
  // import customs
  public importCustoms: string[] = [];
  // ship ports
  public shipPorts: string[] = [];

  // product codes
  public productCodes = '';

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

  convertSelection(): void {
    // 1. convert import customs
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
    // 2. convert product codes
    // convert comma seperator to dash
    this.searchParam[5].value = this.commonUtilitiesService.
      convertCommaSeperatorToDash(this.productCodes);

    // 3. ship ports
    result = '';
    for (const shipPort of this.shipPorts) {
      result = result + shipPort;
      result = result + '~~';
    }
    // get rid of last two chars
    result = result.substr(0, result.lastIndexOf('~~'));
    // convert empty to dash
    result = this.commonUtilitiesService.convertEmptyToDash(result);
    // set to params
    this.searchParam[4].value = result;
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
    this.convertSelection();
    const queryConditions: any = this.getQueryTime();
    const searchParamJson = JSON.stringify(queryConditions);
    return this.downloadHttp.post(this.exportUrl, searchParamJson, httpDownloadOptions).toPromise().then(
      res => {
        const tempRes: any = res;
        if (tempRes._body.code === 201) {
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
    private route: Router) {

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
          that.commonUtilitiesService.sessionTimeout();
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
    this.convertSelection();
    const queryConditions = this.getQueryTime();
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
    $('#import-custom').selectpicker('val', '');
    $('#import-custom').selectpicker('refresh');
    $('#ship-port').selectpicker('val', '');
    $('#ship-port').selectpicker('refresh');

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
}
