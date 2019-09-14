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
import { NEXT } from '@angular/core/src/render3/interfaces/view';
import { DataExcelReportSelectionComponent } from '../components/data-excel-report-selection/data-excel-report-selection.component';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable()
export class DataStatisticsService implements ProcessingDialogCallback {

  // get statistics report processing source id
  private readonly GET_STATISTICS_FIELDS_SOURCE_ID = '001';
  private readonly GET_EXCEL_REPORT_TYPE_ID = '002';
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
  // top N
  public readonly TOP_N = 10;
  // quarter group by field
  public readonly QUARTER_GROUP_BY_FIELD = '季度';
  public readonly YEAR = '年';
  // chart name to chart type
  public readonly CHART_NAME_TO_CHART_TYPE_TABLE: any = {
    '折线图': 'line',
    '柱状图': 'bar',
    '饼状图': 'pie',
  };
  private readonly MAX_GROUPBY_FIELD_LENGTH = 8;

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
  // get excel report setting
  public excelReportSetting(): void {
    this.commonUtilitiesService.showProcessingDialog(this,
      null,
      this.GET_EXCEL_REPORT_TYPE_ID);
  }
  // callback on processing
  public callbackOnProcessing(sourceID: string, data: any): void {
    if (sourceID === this.GET_STATISTICS_FIELDS_SOURCE_ID) {
      // post get statistics fields request
      this.http.post<HttpResponse>(this.statisticsSettingUrl, null, httpOptions).subscribe(
        httpResponse => { this.callbackGettingStatisticsFields(httpResponse); }
      );
    } else if (sourceID === this.GET_EXCEL_REPORT_TYPE_ID) {
      this.http.post<HttpResponse>(this.statisticsSettingUrl, null, httpOptions).subscribe(
        httpResponse => {
          this.callbackGettingExcelReportType(httpResponse);
        }
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
    statisticsFields.setGroupBySubFields(httpResponse.data.groupBySubFields);
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
    modalRef.componentInstance.setGroupBySubFields(statisticsFields.getGroupBySubFields());
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
      this.dataStatisticsComponent.close();
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
      entry.setGroupByField(this.convertQuarterGroupByField(element.groupByField));
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
  // convert group by field if it is quarter, from spring, the group by
  // field is passed as 20191, change it to 2019年1季度
  private convertQuarterGroupByField(groupByField: string): string {
    if (this.statisticsReportQueryData.getGroupByField() === this.QUARTER_GROUP_BY_FIELD) {
      const year: string = groupByField.substring(0, 4);
      const quarter: string = groupByField.substring(4, 5);
      return year + this.YEAR + quarter + this.QUARTER_GROUP_BY_FIELD;
    } else {
      return groupByField;
    }
  }
  // convert statistics report data to data statistics component's options
  public convertStatisticsReportToOptions(
    statisticsReport: StatisticsReportEntry[],
    groupByField: string,
    chartComputeField: string): any {
    const chartType: string = this.convertChartNameToChartType(this.statisticsType);
    if (chartType === 'line') {
      return this.convertStatisticsReportToLineChartOptions(
        statisticsReport,
        groupByField,
        chartComputeField
      );
    } else if (chartType === 'pie') {
      return this.convertStatisticsReportToPieChartOptions(
        statisticsReport,
        groupByField,
        chartComputeField
      );
    } else if (chartType === 'bar') {
      return this.convertStatisticsReportToBarChartOptions(
        statisticsReport,
        groupByField,
        chartComputeField
      );
    } else {
      // nothing to do
    }
  }
  // convert statistics report data to chart opitons
  private convertStatisticsReportToLineChartOptions(
    statisticsReport: StatisticsReportEntry[],
    groupByField: string,
    chartComputeField: string): any {
    // tooltip
    const tooltip = {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    };
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
      // push selected chart field  into legend
      if (element.getFieldName() === chartComputeField) {
        legendData.push(element.getFieldName());
      }
    });
    const legend = {
      data: legendData,
    };
    // xAxis
    const xAxisData = [];
    statisticsReport.forEach(element => {
      xAxisData.push(
        this.commonUtilitiesService.ellipsis(element.getGroupByField(), this.MAX_GROUPBY_FIELD_LENGTH));
    });
    const axisPointer = {
      type: 'shadow'
    };
    const axisLabel = {
      interval: 0,
      rotate: 40,
    };
    const grid = {
      x: 80,
      y: 15,
      x2: 30,
      y2: 80,
    };
    const xAxis = {
      type: 'category',
      data: xAxisData,
      axisPointer: axisPointer,
      axisLabel: axisLabel,
    };
    // yAxis
    const yAxis = [];
    const computeFields = this.statisticsReportQueryData.getComputeFields();
    computeFields.forEach(element => {
      if (element.getFieldName() === chartComputeField) {
        const yAxisEntry = {
          type: 'value',
          name: element.getFieldName(),
          min: this.minComputeValue(statisticsReport, element.getFieldName()),
          max: this.maxComputeValue(statisticsReport, element.getFieldName()),
          position: 'left',
          axisLine: {
            lineStyle: {}
          },
          axisLabel: {
            formatter: '{value}'
          }
        };
        yAxis.push(yAxisEntry);
      }
    });
    // series
    const series = [];
    computeFields.forEach(computeField => {
      if (computeField.getFieldName() === chartComputeField) {
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
      tooltip: tooltip,
      toolBox: toolBox,
      xAxis: xAxis,
      grid: grid,
      yAxis: yAxis,
      series: series,
    };
    return option;
  }
  // convert statistics report data to chart opitons
  private convertStatisticsReportToPieChartOptions(
    statisticsReport: StatisticsReportEntry[],
    groupByField: string,
    chartComputeField: string): any {
    // title
    const title = {
      text: groupByField + '汇总报表',
      subtext: chartComputeField,
      x: 'center'
    };
    // tooltip
    const tooltip = {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    };
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
      // push selected chart field  into legend
      if (element.getFieldName() === chartComputeField) {
        legendData.push(element.getFieldName());
      }
    });
    const legend = {
      bottom: 10,
      data: legendData,
    };
    // series
    const data = [];
    statisticsReport.forEach(statisticsReportEntry => {
      statisticsReportEntry.getComputeValues().forEach(element => {
        if (element.getComputeField() === chartComputeField) {
          const dataEntry = {
            value: element.getComputeValue(),
            name: statisticsReportEntry.getGroupByField(),
          };
          data.push(dataEntry);
        }
      });
    });
    const series = {
      name: groupByField,
      type: 'pie',
      data: data,
      itemStyle: {
        emphasis: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        },
      }
    };
    // finally combine
    const option = {
      title: title,
      tooltip: tooltip,
      toolBox: toolBox,
      legend: legend,
      series: series,
    };
    return option;
  }
  // convert statistics report data to bar chart opitons
  private convertStatisticsReportToBarChartOptions(
    statisticsReport: StatisticsReportEntry[],
    groupByField: string,
    chartComputeField: string): any {
    // tooltip
    const tooltip = {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    };
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
      // push selected chart field  into legend
      if (element.getFieldName() === chartComputeField) {
        legendData.push(element.getFieldName());
      }
    });
    const legend = {
      data: legendData,
    };
    // xAxis
    const xAxisData = [];
    statisticsReport.forEach(element => {
      xAxisData.push(
        this.commonUtilitiesService.ellipsis(element.getGroupByField(), this.MAX_GROUPBY_FIELD_LENGTH));
    });
    const axisPointer = {
      type: 'shadow'
    };
    const axisLabel = {
      interval: 0,
      rotate: 40,
    };
    const grid = {
      x: 80,
      y: 15,
      x2: 30,
      y2: 80,
    };
    const xAxis = {
      type: 'category',
      data: xAxisData,
      axisPointer: axisPointer,
      axisLabel: axisLabel,
    };
    // yAxis
    const yAxis = [];
    const computeFields = this.statisticsReportQueryData.getComputeFields();
    computeFields.forEach(element => {
      if (element.getFieldName() === chartComputeField) {
        const yAxisEntry = {
          type: 'value',
          name: element.getFieldName(),
          min: this.minComputeValue(statisticsReport, element.getFieldName()),
          max: this.maxComputeValue(statisticsReport, element.getFieldName()),
          position: 'left',
          axisLine: {
            lineStyle: {}
          },
          axisLabel: {
            formatter: '{value}'
          }
        };
        yAxis.push(yAxisEntry);
      }
    });
    // series
    const series = [];
    computeFields.forEach(computeField => {
      if (computeField.getFieldName() === chartComputeField) {
        const seriesEntry = {
          name: computeField.getFieldName(),
          type: 'bar',
          data: this.convertComputeValueToDataArray(statisticsReport, computeField.getFieldName()),
        };
        series.push(seriesEntry);
      }
    });
    // finally combine
    const option = {
      tooltip: tooltip,
      toolBox: toolBox,
      grid: grid,
      xAxis: xAxis,
      yAxis: yAxis,
      series: series,
    };
    return option;
  }// min compute value
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
  // max compute value
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
  // get top N statistics report entries sort by graph compute field,
  // if group by field is date, then just get top N without sort.
  public getTopNStatisticsReportEntries(
    statisticsReportEntries: StatisticsReportEntry[],
    selectedChartComputeField: string): StatisticsReportEntry[] {
    // if selected chart compute field is not date, sort by selected chart compute field
    if (selectedChartComputeField !== this.dataStatisticsComponent.DATE_COLUMN) {
      statisticsReportEntries.sort(function (a: StatisticsReportEntry,
        b: StatisticsReportEntry): any {
        // dsc sort
        if (a.getComputeValueOfSpecifiedComputeField(selectedChartComputeField) ===
          b.getComputeValueOfSpecifiedComputeField(selectedChartComputeField)) {
          return 0;
        } else if (a.getComputeValueOfSpecifiedComputeField(selectedChartComputeField) >
          b.getComputeValueOfSpecifiedComputeField(selectedChartComputeField)) {
          return -1;
        } else {
          return 1;
        }
      });
    } else {
      // if selected chart compute field is not date,
      // sorted in spring, so nothing to do
    }
    // get top N statistics report entries
    const ret: StatisticsReportEntry[] = statisticsReportEntries.slice(0, this.TOP_N);
    return ret;
  }
  // convert chart name to chart type
  private convertChartNameToChartType(chartName: string): string {
    return this.CHART_NAME_TO_CHART_TYPE_TABLE[chartName];
  }
  // callback when getting statistics fields
  private callbackGettingExcelReportType(httpResponse: HttpResponse) {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
      return;
    }
    // statistics fields
    const statisticsFields: StatisticsFields = new StatisticsFields();
    statisticsFields.setComputeFields(httpResponse.data.computeFields);
    statisticsFields.setGroupByFields(httpResponse.data.groupByFields);
    statisticsFields.setGroupBySubFields(httpResponse.data.groupBySubFields);
    statisticsFields.setStatisticsTypes(httpResponse.data.statisticsTypes);
    // close processing dialog
    this.commonUtilitiesService.closeProcessingDialog();
    const service: NgbModal = this.modalService;
    // you can not call this.adjustModalOptions,
    // because showUserSettingModal called in html context
    const modalRef = service.open(DataExcelReportSelectionComponent, this.adjustModalOptions());
    // set statistics fields to statistics component
    modalRef.componentInstance.setExcelReportTypes(statisticsFields.getGroupByFields());
  }
}
