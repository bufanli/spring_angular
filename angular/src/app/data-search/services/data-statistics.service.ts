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
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';

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
  private dataStatisticsComponent: any = null;
  // statistics type
  private statisticsType: string;
  // static fields url
  private statisticsFieldsUrl = 'api/statisticsFields';  // URL to web api

  constructor(private commonUtilitiesService: CommonUtilitiesService,
    private http: HttpClient,
    public modalService: NgbModal,
    private currentUserContainer: CurrentUserContainerService) { }
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
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
      return;
    }
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
    // save component
    this.dataStatisticsComponent = modalRef.componentInstance;
    // save service
    modalRef.componentInstance.setStatisticsService(this);
  }
  // statistics report
  public statisticsReport(
    statisticsType: string,
    queryConditions: any,
    groupByField: string,
    computeFields: string[]): void {
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
    options.size = 'lg';
    return options;
  }
  // callback when getting statistics report
  private callbackGettingStatisticsReport(httpResponse: HttpResponse) {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
      return;
    }
    // statistics report
    const statisticsReport: StatisticsReportEntry[] =
      this.convertDataToStatisticsReportArray(httpResponse.data);
    // set options to component
    this.dataStatisticsComponent.setStatisticsReportEntries(statisticsReport);
  }
  private convertDataToStatisticsReportArray(data: any): StatisticsReportEntry[] {
    const result: StatisticsReportEntry[] = new Array<StatisticsReportEntry>(data.length);
    for (let i = 0; i < data.length; i++) {
      const element = data[i];
      const entry: StatisticsReportEntry = new StatisticsReportEntry();
      // group by field
      entry.setGroupByField(element.groupByField);
      // compute values
      const computeValues: any = element.computeValues;
      const finalComputeValues: ComputeValue[] = new Array<ComputeValue>(computeValues.length);
      for (let ii = 0; ii < computeValues.length; ii++) {
        const computeValuesElement = computeValues[ii];
        const computeValue: ComputeValue = new ComputeValue();
        computeValue.setComputeField(computeValuesElement.fieldName);
        computeValue.setComputeValue(computeValuesElement.computeValue);
        finalComputeValues[ii] = computeValue;
      }
      entry.setComputeValues(finalComputeValues);
      result[i] = entry;
    }
    return result;
  }
  // convert statistics report data to data statistics component's options
  public convertStatisticsReportToOptions(statisticsReport: StatisticsReportEntry[]): any {
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
      legendData.push(element.getFieldName());
    });
    const legend = {
      data: legendData,
    };
    // xAxis
    const xAxisData = [];
    statisticsReport.forEach(element => {
      xAxisData.push(element.getGroupByField());
    });

    const xAxis = {
      type: 'category',
      data: xAxisData,
    };
    // yAxis
    const yAxis = [];
    const computeFields = this.statisticsReportQueryData.getComputeFields();
    let count = 0;
    computeFields.forEach(element => {
      let position: string;
      let offset = 0;
      if (count === 1) {
        position = 'left';
      } else {
        position = 'right';
        offset = (count - 1) * 40;
      }
      count++;
      const yAxisEntry = {
        type: 'value',
        name: element.getFieldName(),
        min: this.minComputeValue(statisticsReport, element.getFieldName()),
        max: this.maxComputeValue(statisticsReport, element.getFieldName()),
        position: position,
        offset: offset,
        axisLine: {
          lineStyle: {}
        },
        axisLabel: {
          formatter: '{value}'
        }
      };
      if (count === 1) {
        yAxis.push(yAxisEntry);
      }
    });
    // series
    const series = [];
    count = 0;
    computeFields.forEach(computeField => {
      count = count + 1;
      if (count === 1) {
        const seriesEntry = {
          name: computeField.getFieldName(),
          type: 'line',
          data: this.convertComputeValueToDataArray(statisticsReport, computeField.getFieldName()),
        };
        series.push(seriesEntry);
      }
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
    let result: number = Number.MAX_VALUE;
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
          data.push(computeValue.getComputeValue());
        }
      });
    });
    return data;
  }
}
