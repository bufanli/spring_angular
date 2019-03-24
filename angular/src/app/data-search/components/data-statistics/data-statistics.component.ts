import { Component, OnInit, AfterViewInit, AfterViewChecked, ViewChild, ViewContainerRef, ComponentRef } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { DataStatisticsGraphComponent } from '../data-statistics-graph/data-statistics-graph.component';
import { DataStatisticsOriginalDataComponent } from '../data-statistics-original-data/data-statistics-original-data.component';
import { ComponentFactoryResolver } from '@angular/core/src/render3';
import { StatisticsReportEntry } from '../../entities/statistics-report-entry';

@Component({
  selector: 'app-data-statistics',
  templateUrl: './data-statistics.component.html',
  styleUrls: ['./data-statistics.component.css']
})
export class DataStatisticsComponent implements OnInit, AfterViewInit, AfterViewChecked {

  private readonly STATISTICS_PARAM_ERROR_TITLE = '统计报表参数选择错误';
  public readonly WAIT_STATISTICS_INPUT = '请选择统计图类型，统计维度，计算维度，统计图计算维度';
  public readonly WAIT_STATISTICS_REPORT = '正在计算统计数据，请稍后';
  private readonly STATISTICS_PARAM_ERROR_TYPE = 'warning';
  // statistics types
  public readonly LINE_CHART: string = '折线图';
  public readonly BAR_CHART: string = '柱状图';
  public readonly PIE_CHART: string = '饼状图';
  public options: any;
  // selected statistics type
  type: string = null;
  // selected group by field
  public selectedGroupByField: string = null;
  // statistics group by field
  public groupByFields: string[] = null;
  // selected compute fields
  public selectedComputeFields: string[] = null;
  public seletcedComputeFieldsChanged = false;
  // selected chart compute field
  public selectedChartComputeField: string = null;
  // statics compute fields
  public computeFields: string[] = null;
  // statistics types
  public statisticsTypes: string[] = null;
  // query conditions
  private queryConditions: any = null;
  // statistics data ok
  public isStatisticsDataOK = false;
  // statistics data ng, threre are two sub status
  public isWaitingStatisticsInput = true;
  public isWaitingStatisticsReport = false;

  // statistics service
  private statisticsService: any = null;
  // statistics report entries
  private statisticsReportEntries: StatisticsReportEntry[] = null;

  private resolver: ComponentFactoryResolver;
  @ViewChild('statisticsContainer', { read: ViewContainerRef }) container: ViewContainerRef;
  componnetRefDataStatisticsGraph: ComponentRef<DataStatisticsGraphComponent>;
  componentRefDataStatisticsOriginalData: ComponentRef<DataStatisticsOriginalDataComponent>;

  constructor(private activeModal: NgbActiveModal,
    private commonUtilities: CommonUtilitiesService) { }

  createComponent(type: string) {
    this.container.clear();
    if (type === 'graph') {
      const factory = this.resolver.resolveComponentFactory(DataStatisticsGraphComponent);
      this.componnetRefDataStatisticsGraph = this.container.createComponent(factory);
      this.componnetRefDataStatisticsGraph.instance.setStatisticsReportEntries(this.statisticsReportEntries);
      // this.componentRef.instance.output.subscribe((msg: string) => console.log(msg));
    } else if (type === 'original-data') {
      const factory = this.resolver.resolveComponentFactory(DataStatisticsOriginalDataComponent);
      this.componentRefDataStatisticsOriginalData = this.container.createComponent(factory);
      this.componentRefDataStatisticsOriginalData.instance.setStatisticsReportEntries(this.statisticsReportEntries);
      this.componentRefDataStatisticsOriginalData.instance.setSelectedGroupbyField(this.selectedGroupByField);
      this.componentRefDataStatisticsOriginalData.instance.setSelectedComputeFields(this.selectedComputeFields);
      // this.componentRef.instance.output.subscribe((msg: string) => console.log(msg));
    }
  }
  public onChangeComputeFields() {
    this.seletcedComputeFieldsChanged = true;
  }
  // get statistics types
  public getStatisticsTypes(): string[] {
    return this.statisticsTypes;
  }
  public setStatisticsTypes(statisticsTypes: string[]): void {
    this.statisticsTypes = statisticsTypes;
    this.setSelectOptions('#statistics-type', false);
  }

  // set statistics service
  public setStatisticsService(statisticsService: any): void {
    this.statisticsService = statisticsService;
  }
  // set chart options
  public setOptions(options: any): void {
    this.isStatisticsDataOK = true;
    this.options = options;
  }
  // set statistics report entries
  public setStatisticsReportEntries(statisticsReportEntries: StatisticsReportEntry[]): void {
    // set statistics report entries
    this.statisticsReportEntries = statisticsReportEntries;
    // set data ok flag
    this.isStatisticsDataOK = true;
    // create statistics graph component
    this.createComponent('graph');
  }
  // get group by fields
  public getGroupByFields(): string[] {
    return this.groupByFields;
  }
  public setGroupByFields(groupByFields: string[]) {
    this.groupByFields = groupByFields;
    this.setSelectOptions('#group-fields', true);
  }
  // get compute fields
  public getComputeFields(): string[] {
    return this.computeFields;
  }
  // set compute fields
  public setComputeFields(computeFields: string[]) {
    this.computeFields = computeFields;
    this.setSelectOptions('#compute-fields', true);
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
  public exportStatisticsReport() {
    if (this.checkStatisticsParameters() === false) {
      // show error dialog
      this.commonUtilities.showSimpleDialog(this.STATISTICS_PARAM_ERROR_TITLE,
        this.WAIT_STATISTICS_INPUT,
        this.STATISTICS_PARAM_ERROR_TYPE);
    } else {
      // call service to get statistics report
      this.statisticsService.statisticsReport(
        this.type,
        this.queryConditions,
        this.selectedGroupByField,
        this.selectedComputeFields
      );
    }
  }
  // check statistics parameters
  private checkStatisticsParameters(): boolean {
    // necessary parameter is not selected
    if ((this.type === null) ||
      (this.selectedComputeFields === null) ||
      (this.selectedGroupByField === null) ||
      (this.selectedChartComputeField === null)) {
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
    // todo
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
    this.setSelectOptions('#compute-fields', true);
    // chart compute fields
    this.setSelectOptions('#chart-compute-field', false);

  }
  // if selected computed fields changed, refresh statistics computed field select options
  ngAfterViewChecked(): void {
    if (this.seletcedComputeFieldsChanged === true) {
      $('#chart-compute-field').selectpicker('refresh');
      this.seletcedComputeFieldsChanged = false;
    }
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
