import { ProcessingDialogCallback } from 'src/app/common/interfaces/processing-dialog-callback';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { ComputeField } from '../entities/compute-field';
import { StatisticsReportQueryData } from '../entities/statistics-report-query-data';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { NgbModal, NgbModalOptions, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { DataStatisticsComponent } from '../components/data-statistics/data-statistics.component';
import { Injectable } from '@angular/core';
import { StatisticsFields } from '../entities/statistics-fields';
import { StatisticsReportEntry } from '../entities/statistics-report-entry';
import { ComputeValue } from '../entities/compute-value';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable()
export class DataStatisticsService implements ProcessingDialogCallback {

  // get statistics report processing source id
  private readonly GET_STATISTICS_FIELDS_SOURCE_ID = '001';
  // query conditions
  private queryConditions: any = null;
  // statistics report url
  private statisticsReportsUrl = 'api/statisticsReport';  // URL to web api
  // statistics settings url
  private statisticsSettingUrl = 'api/statisticsSetting';  // URL to web api
  // statistics report query data
  private statisticsReportQueryData: StatisticsReportQueryData = null;
  // statistics component
  private dataStatisticsComponent: DataStatisticsComponent = null;
  // statistics type
  private statisticsType: string;
  // static fields url
  private statisticsFieldsUrl = 'api/statisticsFields';  // URL to web api
  constructor(private commonUtilitiesService: CommonUtilitiesService,
    private http: HttpClient,
    public modalService: NgbModal) { }
  // get statistic report
  public statisticsSetting(queryConditions: any): void {
    this.queryConditions = queryConditions;
    this.commonUtilitiesService.showProcessingDialog(this,
      null,
      this.GET_STATISTICS_FIELDS_SOURCE_ID);
  }
  // callback on processing
  public callbackOnProcessing(sourceID: string, data: any): void {
    if (sourceID === this.GET_STATISTICS_FIELDS_SOURCE_ID) {
      // post get statistics fields request
      this.http.post<HttpResponse>(this.statisticsSettingUrl, null, httpOptions).subscribe(
        httpResponse => { this.callbackGettingStatisticsFields(httpResponse); }
      );
    }
  }
  // callback when getting statistics fields
  private callbackGettingStatisticsFields(httpResponse: HttpResponse) {
    // statistics fields
    const statisticsFields: StatisticsFields = new StatisticsFields();
    statisticsFields.setComputeFields(httpResponse.data.computeFields);
    statisticsFields.setGroupByFields(httpResponse.data.groupByFields);
    statisticsFields.setStatisticsTypes(httpResponse.data.statisticsTypes);
    // close processing dialog
    this.commonUtilitiesService.closeProcessingDialog();
    const service: NgbModal = this.modalService;
    // you can not call this.adjustModalOptions,
    // because showUserSettingModal called in html context
    const modalRef = service.open(DataStatisticsComponent, this.adjustModalOptions());
    // set statistics fields to statistics component
    modalRef.componentInstance.setComputeFields(statisticsFields.getComputeFields());
    modalRef.componentInstance.setGroupByFields(statisticsFields.getGroupByFields());
    modalRef.componentInstance.setStatisticsTypes(statisticsFields.getStatisticsTypes());
    // set query conditions
    modalRef.componentInstance.setQueryConditions(this.queryConditions);
  }
  // statistics report
  public statisticsReport(
    dataStatisticsComponent: DataStatisticsComponent,
    statisticsType: string,
    queryConditions: any,
    groupByField: string,
    computeFields: string[]): void {
    // get statistics report callback
    // post statistics report request
    const statisticsReportQueryData: StatisticsReportQueryData
      = new StatisticsReportQueryData();
    // group by field
    statisticsReportQueryData.setGroupByField(groupByField);
    // compute fields
    const finalComputeFields: ComputeField[] = [];
    computeFields.forEach(element => {
      const computeField: ComputeField = new ComputeField();
      computeField.setFieldName(element);
      computeField.setComputeType('SUM');
      finalComputeFields.push(computeField);
    });
    statisticsReportQueryData.setComputeFields(finalComputeFields);
    // set query conditons
    statisticsReportQueryData.setQueryConditions(queryConditions);
    // save statistics report query data in advance
    this.statisticsReportQueryData = statisticsReportQueryData;
    // save data statistics component
    this.dataStatisticsComponent = dataStatisticsComponent;
    // save statistics type
    this.statisticsType = statisticsType;
    // post statistics report request
    this.http.post<HttpResponse>(this.statisticsReportsUrl, statisticsReportQueryData, httpOptions).subscribe(
      httpResponse => { this.callbackGettingStatisticsReport(httpResponse); });
  }
  // adjust modal options
  // if don't adjust modal options, modal will not be shown correctly
  private adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    return options;
  }
  // callback when getting statistics report
  private callbackGettingStatisticsReport(httpResponse: HttpResponse) {
    // statistics report
    const statisticsReport: StatisticsReportEntry[] = httpResponse.data;
    // convert statistics report to options
    const options: any = this.convertStatisticsReportToOptions(statisticsReport);
    // set options to component
    this.dataStatisticsComponent.setOptions(options);
  }
  // convert statistics report data to data statistics component's options
  private convertStatisticsReportToOptions(statisticsReport: StatisticsReportEntry[]): any {
    if (this.statisticsType === this.dataStatisticsComponent.LINE_CHART) {
      // line chart
      return this.convertStatisticsReportToLineChartOptions(statisticsReport);
    } else if (this.statisticsType === this.dataStatisticsComponent.PIE_CHART) {
      // pie chart
      return this.convertStatisticsReportToPieChartOptions(statisticsReport);
    } else if (this.statisticsType === this.dataStatisticsComponent.BAR_CHART) {
      // bar chart
      return this.convertStatisticsReportToBarChartOptions(statisticsReport);
    } else {
      // nothing to do
    }
    return null;
  }
  // convert statistics report data to line chart opitons
  private convertStatisticsReportToLineChartOptions(statisticsReport: StatisticsReportEntry[]): any {
    // tool box
    const dataView = {
      show: true,
      readOnly: true,
    };
    const restore = {
      show: false,
    };
    const saveAsImage = {
      show: true,
    };
    const feature = {
      dataView: dataView,
      restore: restore,
      saveAsImage: saveAsImage,
    };
    const toolBox = {
      feature: feature,
    };
    // legend
    const legendData = [];
    this.statisticsReportQueryData.getComputeFields().forEach(element => {
      legendData.push(element);
    });
    const legend = {
      data: legendData,
    };
    // xAxis
    const xAxisData = [];
    statisticsReport.forEach(element => {
      xAxisData.push(element.getGroupByField());
    });

    const firstXAxis = {
      type: 'catetory',
      data: xAxisData,
    };
    const xAxis = [firstXAxis];
    // yAxis
    const yAxis = [];
    const computeFields = this.statisticsReportQueryData.getComputeFields();
    const count = 1;
    computeFields.forEach(element => {
      let position: string;
      if (count === 1) {
        position = 'left';
      } else {
        position = 'right';
      }
      const yAxisEntry = {
        type: 'value',
        name: element.getFieldName(),
        min: this.minComputeValue(statisticsReport, element.getFieldName()),
        max: this.maxComputeValue(statisticsReport, element.getFieldName()),
        position: position,
        axisLine: {
          lineStyle: {}
        },
        axisLabel: {
          formatter: '{value}'
        }
      };
      yAxis.push(yAxisEntry);
    });
    // series
    const series = [];
    computeFields.forEach(computeField => {
      const seriesEntry = {
        name: computeField.getFieldName(),
        type: 'line',
        data: this.convertComputeValueToDataArray(statisticsReport, computeField.getFieldName()),
      };
      series.push(seriesEntry);
    });
    // finally combine
    const option = {
      toolBox: toolBox,
      legend: legend,
      xAxis: xAxis,
      yAxis: yAxis,
      series: series,
    };
    return option;
  }
  // convert statistics report data to pie chart opitons
  private convertStatisticsReportToPieChartOptions(statisticsReport: StatisticsReportEntry[]): any {
    return null;
  }
  // convert statistics report data to bar chart opitons
  private convertStatisticsReportToBarChartOptions(statisticsReport: StatisticsReportEntry[]): any {
    return null;
  }
  private minComputeValue(statisticsReport: StatisticsReportEntry[], computeField: string): Number {
    let result = Number.MAX_VALUE;
    statisticsReport.forEach(element => {
      const computeValues: ComputeValue[] = element.getComputeValues();
      computeValues.forEach(computeValue => {
        if (computeValue.getComputeField() === computeField) {
          if (computeValue.getComputeValue() < result) {
            result = computeValue.getComputeValue();
          }
        }
      });
    });
    return result;
  }
  private maxComputeValue(statisticsReport: StatisticsReportEntry[], computeField: string): Number {
    let result = Number.MIN_VALUE;
    statisticsReport.forEach(element => {
      const computeValues: ComputeValue[] = element.getComputeValues();
      computeValues.forEach(computeValue => {
        if (computeValue.getComputeField() === computeField) {
          if (computeValue.getComputeValue() > result) {
            result = computeValue.getComputeValue();
          }
        }
      });
    });
    return result;
  }
  private convertComputeValueToDataArray(statisticsReport: StatisticsReportEntry[], computeField: string): Number[] {
    const data = [];
    statisticsReport.forEach(element => {
      const computeValues = element.getComputeValues();
      computeValues.forEach(computeValue => {
        if (computeValue.getComputeField() === computeField) {
          data.push(computeValue.getComputeValue);
        }
      });
    });
    return data;
  }
}
