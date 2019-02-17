import { ProcessingDialogCallback } from 'src/app/common/interfaces/processing-dialog-callback';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { ComputeField } from '../entities/compute-field';
import { StatisticsReportQueryData } from '../entities/statistics-report-query-data';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { NgbModal, NgbModalOptions, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { DataStatisticsComponent } from '../components/data-statistics/data-statistics.component';
import { Injectable } from '@angular/core';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable()
export class DataStatisticsService implements ProcessingDialogCallback {

  // get statistics report processing source id
  private readonly GET_STATISTICS_REPORT_SOURCE_ID = '001';
  // statistics report url
  private statisticsReportsUrl = 'api/statisticsReport';  // URL to web api
  constructor(private commonUtilitiesService: CommonUtilitiesService,
    private http: HttpClient,
    public modalService: NgbModal) { }
  // get statistic report
  public statisticsReport(queryConditions: any): void {
    this.commonUtilitiesService.showProcessingDialog(this,
      queryConditions,
      this.GET_STATISTICS_REPORT_SOURCE_ID);
  }
  // callback on processing
  public callbackOnProcessing(sourceID: string, data: any): void {
    if (sourceID === this.GET_STATISTICS_REPORT_SOURCE_ID) {
      // get statistics report callback
      // post statistics report request
      const statisticsReportQueryData: StatisticsReportQueryData
        = new StatisticsReportQueryData();
      // group by field
      statisticsReportQueryData.setGroupByField('收货人');
      // compute fields
      const computeFields: ComputeField[] = [];
      const computeField1: ComputeField = new ComputeField();
      const computeField2: ComputeField = new ComputeField();
      computeField1.setFieldName('重量');
      computeField1.setComputeType('SUM');
      computeField2.setFieldName('件数');
      computeField2.setComputeType('SUM');
      computeFields.push(computeField1);
      computeFields.push(computeField2);
      statisticsReportQueryData.setComputeFields(computeFields);
      // set query conditons
      statisticsReportQueryData.setGroupByField(data);
      // post statistics report request
      this.http.post<HttpResponse>(this.statisticsReportsUrl, statisticsReportQueryData, httpOptions).subscribe(
        httpResponse => { this.callbackGettingStatisticsReport(httpResponse); }
      );
    }
  }
  // callback when getting statistics report
  private callbackGettingStatisticsReport(httpResponse: HttpResponse) {
    // close processing dialog
    this.commonUtilitiesService.closeProcessingDialog();
    const service: NgbModal = this.modalService;
    // you can not call this.adjustModalOptions,
    // because showUserSettingModal called in html context
    const modalRef = service.open(DataStatisticsComponent, this.adjustModalOptions());
  }
  // adjust modal options
  // if don't adjust modal options, modal will not be shown correctly
  private adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    return options;
  }
}
