import { Injectable } from '@angular/core';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { ProcessingDialogCallback } from 'src/app/common/interfaces/processing-dialog-callback';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { QueryCondition } from '../entities/query-condition';
import { UUID } from 'angular2-uuid';
import { DataExcelReportSelectionComponent } from '../components/data-excel-report-selection/data-excel-report-selection.component';
import { NgbModalOptions, NgbModalConfig, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ExcelReportSettingData } from '../entities/excel-report-setting-data';
import { Http, ResponseContentType, Headers } from '@angular/http';
import { saveAs as importedSaveAs } from 'file-saver';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
// tslint:disable-next-line: deprecation
const head = new Headers({ 'Content-Type': 'application/json' });
const httpDownloadOptions = {
  headers: head,
  // tslint:disable-next-line: deprecation
  responseType: ResponseContentType.Blob,
};
@Injectable()
export class ExcelReportService implements ProcessingDialogCallback {

  // excel report query conditions
  private excelReportQueryConditionsUrl = 'api/getExcelReportConditions';  // URL to web api
  // excel report types
  private excelReportTypesUrl = 'api/getExcelReportTypes';  // URL to web api
  // export excel report
  private exportExcelReportUrl = 'api/exportExcelReport';
  private EXCEL_REPORT_ERROR_TITLE = '汇总报告执行失败';
  // fixed excel report query condtions
  private FIXED_EXCEL_REPORT_QUERY_CONDITIONS: Set<String> = new Set(['月份', '海关编码', '进出口']);
  private EXCLUDE_EXCEL_REPORT_TYPES: Set<String> = new Set([
    '进出口',
    '日期',
    '申报单价',
    '申报总价',
    '申报币制',
    '美元单价',
    '美元总价',
    '美元币制',
    '人民币总价',
    '申报数量',
    '申报数量单位',
    '法定重量',
    '法定单位',
    '毛重',
    '净重',
    '重量单位',
    '件数',
    '运费／率',
    '运费币制',
    '保险费／率',
    '保险费币制',
    '杂费／率',
    '杂费币制',
    '']);

  // excel report query conditions
  private excelReportQueryConditions = null;
  // excel report types
  private excelReportTypes: string[] = null;

  private readonly GET_EXCEL_REPORT_TYPE_ID = '001';
  constructor(
    private commonUtilitiesService: CommonUtilitiesService,
    private http: HttpClient,
    private currentUserContainer: CurrentUserContainerService,
    private modalService: NgbModal,
    // tslint:disable-next-line: deprecation
    private httpDownload: Http) {
  }
  // get excel report setting
  public excelReportSetting(): void {
    this.commonUtilitiesService.showProcessingDialog(
      this,
      null,
      this.GET_EXCEL_REPORT_TYPE_ID);
  }
  // callback on processing
  public callbackOnProcessing(sourceID: string, data: any): void {
    if (sourceID === this.GET_EXCEL_REPORT_TYPE_ID) {
      this.http.post<HttpResponse>(this.excelReportQueryConditionsUrl, null, httpOptions).subscribe(
        httpResponse => {
          this.getExcelReportQueryConditionsNotification(httpResponse);
        }
      );
    }
  }
  // callback when getting statistics fields
  private getExcelReportQueryConditionsNotification(httpResponse: HttpResponse) {
    this.commonUtilitiesService.closeProcessingDialog();
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
      return;
    } else if (httpResponse.code !== 200) {
      this.commonUtilitiesService.showSimpleDialog(
        this.EXCEL_REPORT_ERROR_TITLE,
        httpResponse.message,
        'info'
      );
    } else {
      this.excelReportQueryConditions = this.convertQueryConditions(httpResponse.data);
      // get excel report types
      this.getExcelReportTypes();
    }
  }
  // convert query conditions from http response to QueryCondition
  private convertQueryConditions(queryConditions: any): QueryCondition[] {
    const queryConditionsRet = [];
    for (let index = 0; index < queryConditions.length; index++) {
      if (this.FIXED_EXCEL_REPORT_QUERY_CONDITIONS.has(queryConditions[index].key) === false) {
        continue;
      }
      const tempQueryCondition = queryConditions[index];
      const uuid = UUID.UUID();
      const tempQueryConditionRet = new QueryCondition(
        tempQueryCondition.key,
        tempQueryCondition.value,
        tempQueryCondition.type,
      );
      // set uuid
      tempQueryConditionRet.setUUID(uuid);
      queryConditionsRet.push(tempQueryConditionRet);
    }
    return queryConditionsRet;
  }
  // get excel report types
  private getExcelReportTypes(): void {
    this.http.post<HttpResponse>(this.excelReportTypesUrl, null, httpOptions).subscribe(
      httpResponse => {
        this.getExcelReportTypesNotification(httpResponse);
      }
    );
  }
  // get excel report types notification
  private getExcelReportTypesNotification(httpResponse: any): void {
    this.excelReportTypes = httpResponse.data;
    // exclude abundant excel report types
    const excludedExcelReportTypes = [];
    this.excelReportTypes.forEach(element => {
      // get rid of huizong
      let temp: string = null;
      if (element.endsWith('汇总')) {
        temp = element.substring(0, element.length - '汇总'.length);
      }
      if (this.EXCLUDE_EXCEL_REPORT_TYPES.has(temp) === false) {
        excludedExcelReportTypes.push(element);
      }
    });
    // close processing dialog
    this.commonUtilitiesService.closeProcessingDialog();
    const service: NgbModal = this.modalService;
    // you can not call this.adjustModalOptions,
    // because showUserSettingModal called in html context
    const modalRef = service.open(DataExcelReportSelectionComponent, this.adjustModalOptions());
    // set excel report types to component
    modalRef.componentInstance.setExcelReportTypes(excludedExcelReportTypes);
    // set excel report query conditions to component
    modalRef.componentInstance.setExcelReportQueryConditions(this.excelReportQueryConditions);
    // set data statistics service
    modalRef.componentInstance.setExcelReportService(this);
  }
  // adjust modal options
  // if don't adjust modal options, modal will not be shown correctly
  private adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    options.size = 'lg';
    return options;
  }
  // excel report
  public excelReport(excelReportSettingData: ExcelReportSettingData): void {
    this.excelReportImpl(excelReportSettingData);
  }
  // download data to file
  private async excelReportImpl(excelReportSettingData: ExcelReportSettingData): Promise<void> {
    const excelReportSettingJson = JSON.stringify(excelReportSettingData);
    return this.httpDownload.post(this.exportExcelReportUrl, excelReportSettingJson, httpDownloadOptions).toPromise().then(
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
            importedSaveAs(res.blob(), decodeURI(fileName));
          } else {
            importedSaveAs(res.blob());
          }
        }
      });
  }
}
