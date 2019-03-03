import { Component, OnInit, AfterContentInit, AfterViewChecked, AfterViewInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';

@Component({
  selector: 'app-data-statistics',
  templateUrl: './data-statistics.component.html',
  styleUrls: ['./data-statistics.component.css']
})
export class DataStatisticsComponent implements OnInit, AfterViewInit {

  private readonly STATISTICS_PARAM_ERROR_TITLE = '统计报表参数选择错误';
  public readonly WAIT_STATISTICS_INPUT = '请选择统计图类型，统计维度，统计变量';
  public readonly WAIT_STATISTICS_REPORT = '正在计算统计数据，请稍后';
  private readonly STATISTICS_PARAM_ERROR_TYPE = 'warning';
  // statistics types
  public readonly LINE_CHART: string = '折线图';
  public readonly BAR_CHART: string = '柱状图';
  public readonly PIE_CHART: string = '饼状图';
  private readonly STATISTICS_TYPES: string[] = [this.LINE_CHART, this.PIE_CHART, this.BAR_CHART];
  public options: any;
  // selected statistics type
  type: string = null;
  // selected group by field
  public selectedGroupByField: string = null;
  // statistics group by field
  public groupByFields: string[] = ['进口关区', '原产国', '装货港'];
  // selected compute fields
  public selectedComputeFields: string[] = null;
  // statics compute fields
  public computeFields: string[] = ['重量', '数量', 'TEU', '票数'];
  // query conditions
  private queryConditions: any = null;
  // statistics data ok
  public isStatisticsDataOK = false;
  // statistics data ng, threre are two sub status
  public isWaitingStatisticsInput = true;
  public isWaitingStatisticsReport = false;

  constructor(private activeModal: NgbActiveModal,
    private commonUtilities: CommonUtilitiesService) { }
  // get statistics types
  public getStatisticsTypes(): string[] {
    return this.STATISTICS_TYPES;
  }
  // change status to get statistics report
  private changeStatusToGetStatisticsReport(): void {
    this.isStatisticsDataOK = false;
    this.isWaitingStatisticsReport = true;
  }
  // set chart options
  public setOptions(options: any): void {
    this.isStatisticsDataOK = true;
    this.options = options;
  }
  // get group by fields
  public getGroupByFields(): string[] {
    return this.groupByFields;
  }
  public setGroupByFields(groupByFields: string[]) {
    this.groupByFields = groupByFields;
  }
  // get compute fields
  public getComputeFields(): string[] {
    return this.computeFields;
  }
  // set compute fields
  public setComputeFields(computeFields: string[]) {
    this.computeFields = computeFields;
  }
  // set query conditions
  public setQueryConditions(queryConditions: any) {
    this.queryConditions = queryConditions;
  }
  // get query conditions
  public getQueryConditions(): any {
    return this.queryConditions;
  }
  // export top ten statistics report
  public exportTopTenStatisticsReport() {
    if (this.checkStatisticsParameters() === false) {
      // show error dialog
      this.commonUtilities.showSimpleDialog(this.STATISTICS_PARAM_ERROR_TITLE,
        this.WAIT_STATISTICS_INPUT,
        this.STATISTICS_PARAM_ERROR_TYPE);
    } else {
      //
    }
  }
  // check statistics parameters
  private checkStatisticsParameters(): boolean {
    // necessary parameter is not selected
    if ((this.type === null) ||
      (this.selectedComputeFields === null) ||
      (this.selectedGroupByField === null)) {
      return false;
    } {
      // all necessary paramters is all selected
      return true;
    }
  }
  // set statistics report options
  public setStatisticsReportOptions(options: any): void {
    this.options = options;
  }
  ngOnInit() {
    const xAxisData = [];
    const data1 = [];
    const data2 = [];

    for (let i = 0; i < 100; i++) {
      xAxisData.push('category' + i);
      data1.push((Math.sin(i / 5) * (i / 5 - 10) + i / 6) * 5);
      data2.push((Math.cos(i / 5) * (i / 5 - 10) + i / 6) * 5);
    }

    this.options = {
      legend: {
        data: ['bar', 'bar2'],
        align: 'left'
      },
      tooltip: {
        feature: {
          saveAsImage: {},
        }
      },
      xAxis: {
        data: xAxisData,
        silent: false,
        splitLine: {
          show: false
        }
      },
      yAxis: {
      },
      series: [{
        name: 'bar',
        type: 'bar',
        data: data1,
        animationDelay: function (idx) {
          return idx * 10;
        }
      }, {
        name: 'bar2',
        type: 'bar',
        data: data2,
        animationDelay: function (idx) {
          return idx * 10 + 100;
        }
      }],
      animationEasing: 'elasticOut',
      animationDelayUpdate: function (idx) {
        return idx * 5;
      }
    };
  }
  public close(): void {
    this.activeModal.close();
  }
  // just for select picker
  ngAfterViewInit(): void {
    // if call selectpicker in ngOnInit, select control will not be shown for some reason
    // but call selectpicker can resolve this issue in ngAfterViewInit
    // statistics types
    this.setSelectOptions('#statistics-type', false);
    // group by fields
    this.setSelectOptions('#group-fields', true);
    // compute fields
    this.setSelectOptions('#compute-types', true);
  }

  // init select picker
  private setSelectOptions(id: string, liveSearch: boolean): void {
    $(id).selectpicker({
      'liveSearch': liveSearch,
    });
    $(id).selectpicker('val', '');
    $(id).selectpicker('refresh');
  }
}