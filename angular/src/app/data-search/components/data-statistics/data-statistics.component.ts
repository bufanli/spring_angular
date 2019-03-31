import { Component, OnInit, AfterViewInit, AfterViewChecked, } from '@angular/core';
import { ViewChild, ViewContainerRef, ComponentRef, ComponentFactoryResolver } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { DataStatisticsGraphComponent } from '../data-statistics-graph/data-statistics-graph.component';
import { DataStatisticsOriginalDataComponent } from '../data-statistics-original-data/data-statistics-original-data.component';
import { StatisticsReportEntry } from '../../entities/statistics-report-entry';

@Component({
  selector: 'app-data-statistics',
  templateUrl: './data-statistics.component.html',
  styleUrls: ['./data-statistics.component.css']
})
export class DataStatisticsComponent implements OnInit, AfterViewInit, AfterViewChecked {

  private readonly STATISTICS_PARAM_ERROR_TITLE = '统计报表参数选择错误';
  public readonly WAIT_STATISTICS_INPUT = '请选择统计图类型，统计列，计算列，统计图计算列';
  public readonly WAIT_STATISTICS_REPORT = '正在计算统计数据，请稍后';
  private readonly STATISTICS_PARAM_ERROR_TYPE = 'warning';
  // date column name
  public readonly DATE_COLUMN = '日期';

  // selected statistics type
  type: string = null;
  // selected group by field
  public selectedGroupByField: string = null;
  // selected detail date field
  public selectedDetailDateField: string = null;
  // statistics group by field
  public groupByFields: string[] = null;
  // statistics group by sub field
  public groupBySubFields: string[] = null;
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
  // showing component name
  private showingComponentName = 'graph';

  // statistics service
  private statisticsService: any = null;
  // statistics report entries
  private statisticsReportEntries: StatisticsReportEntry[] = null;

  @ViewChild('statisticsContainer', { read: ViewContainerRef }) container: ViewContainerRef;
  componnetRefDataStatisticsGraph: ComponentRef<DataStatisticsGraphComponent>;
  componentRefDataStatisticsOriginalData: ComponentRef<DataStatisticsOriginalDataComponent>;

  constructor(private activeModal: NgbActiveModal,
    private commonUtilities: CommonUtilitiesService,
    private resolver: ComponentFactoryResolver) { }

  createComponent(type: string) {
    this.container.clear();
    if (type === 'graph') {
      this.showingComponentName = 'graph';
      const factory = this.resolver.resolveComponentFactory(DataStatisticsGraphComponent);
      this.componnetRefDataStatisticsGraph = this.container.createComponent(factory);
      // get top N statistics report entries
      const topNStatisticsReportEntries: StatisticsReportEntry[]
        = this.statisticsService.getTopNStatisticsReportEntries(
          this.statisticsReportEntries,
          this.selectedChartComputeField
        );
      this.componnetRefDataStatisticsGraph.instance.setStatisticsReportEntries(topNStatisticsReportEntries);
      this.componnetRefDataStatisticsGraph.instance.setChartComputeField(this.selectedChartComputeField);
      this.componnetRefDataStatisticsGraph.instance.setDataStatisticsService(this.statisticsService);
      this.componnetRefDataStatisticsGraph.instance.setGroupByField(this.convertDateGroupByField());
      // this.componentRef.instance.output.subscribe((msg: string) => console.log(msg));
    } else if (type === 'original-data') {
      this.showingComponentName = 'original-data';
      const factory = this.resolver.resolveComponentFactory(DataStatisticsOriginalDataComponent);
      this.componentRefDataStatisticsOriginalData = this.container.createComponent(factory);
      this.componentRefDataStatisticsOriginalData.instance.setStatisticsReportEntries(this.statisticsReportEntries);
      this.componentRefDataStatisticsOriginalData.instance.setSelectedGroupbyField(this.selectedGroupByField);
      this.componentRefDataStatisticsOriginalData.instance.setSelectedComputeFields(this.selectedComputeFields);
      this.componentRefDataStatisticsOriginalData.instance.setDataStatisticsService(this.statisticsService);
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
  // set statistics report entries
  public setStatisticsReportEntries(statisticsReportEntries: StatisticsReportEntry[]): void {
    // set statistics report entries
    this.statisticsReportEntries = statisticsReportEntries;
    // set data ok flag
    this.isStatisticsDataOK = true;
    this.createComponent(this.showingComponentName);
  }
  // get group by fields
  public getGroupByFields(): string[] {
    return this.groupByFields;
  }
  public setGroupByFields(groupByFields: string[]) {
    this.groupByFields = groupByFields;
    this.setSelectOptions('#group-fields', true);
  }
  // set group by sub fields
  public setGroupBySubFields(groupBySubFields: string[]): void {
    this.selectedDetailDateField = groupBySubFields[0];
    this.groupBySubFields = groupBySubFields;
  }
  // get group by sub fields
  public getGroupBySubFields(): string[] {
    return this.groupBySubFields;
  }
  // get compute fields
  public getComputeFields(): string[] {
    return this.computeFields;
  }
  // set compute fields
  public setComputeFields(computeFields: string[]) {
    this.computeFields = computeFields;
    this.setSelectOptions('#compute-fields', true);
    // select all in advance
    this.selectedComputeFields = computeFields;
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
        // convert detail date field to selected group by field
        // if selectable group by field is DATE_COLUMN
        this.convertDateGroupByField(),
        this.selectedComputeFields
      );
    }
  }
  // if selectable group by field is date,
  // convert group by field to selectable group by field
  private convertDateGroupByField(): string {
    if (this.selectedGroupByField === this.DATE_COLUMN) {
      return this.selectedDetailDateField;
    } else {
      return this.selectedGroupByField;
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
  ngOnInit() {
    // nothing to do
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
    // detail date fields
    this.setSelectOptions('#detail-date-fields', false);
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
  // get selectable detail date fields
  public getSelectableDetailDateFields(): string[] {
    return this.getGroupBySubFields();
  }
  // tell if date is selected as group by field
  public isDateIsSelected(): boolean {
    if (this.selectedGroupByField === (this.DATE_COLUMN)) {
      return true;
    } else {
      return false;
    }
  }
}
