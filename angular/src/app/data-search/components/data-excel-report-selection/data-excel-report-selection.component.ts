import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Header } from 'src/app/common/entities/header';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-data-excel-report-selection',
  templateUrl: './data-excel-report-selection.component.html',
  styleUrls: ['./data-excel-report-selection.component.css']
})
export class DataExcelReportSelectionComponent implements OnInit, AfterViewInit {
  public readonly SELECT_STATISTICS_REPORT_TYPE = '请选择汇总设定';
  public readonly PRODUCT_CODE = '请选择商品编码';
  public readonly REPORT_MONTH = '请选择报告月份';
  public readonly EXPORT_OR_NOT = '请选择进出口';
  public readonly EXPORT = '出口';
  public readonly IMPORT = '进口';
  public readonly EXPORT_OR_NOT_ARRAY = [this.EXPORT, this.IMPORT];
  public readonly CLOSE_BUTTON = '关闭';
  public readonly EXPORT_EXCEL_REPORT = '导出汇总报告';
  public readonly EXCEL_REPORT_TYPES_TITLE = '导出汇总报告';
  public readonly EXCEL_REPORT_TYPES_FIELD = 'excel-report-type';
  // model data
  public selectedProductCode = '';
  public selectedReportMonth = '';
  public selectedExportOrNot = '';
  // excel report types
  public excelReportTypes = null;
  public excelReportTypesTableValues: any[] = null;
  // data statistics service
  private dataStatisticsService: any = null;
  constructor(private activeModal: NgbActiveModal) { }

  ngOnInit() {
  }
  ngAfterViewInit() {
    // init excel report types table
    this.initExcelReportTypes();
    // convert excel report types to table data
    this.convertExcelReportTypesToTableData();
    // load data into excel report types table
    this.loadExcelReportTypesIntoTable();
    // init selector
    this.setSelectOptions('#export-or-not', false);

  }
  // excel report
  public excelReport(): void {
    // this.dataStatisticsService.excelReportSetting();
  }
  // public set data statistics service
  public setDataStatisticsService(dataStatisticsService: any): void {
    this.dataStatisticsService = dataStatisticsService;
  }
  // close dialogue
  public close(): void {
    this.activeModal.close();
  }
  // set group by fields
  // convert excel report types from string arrry to {}
  public setExcelReportTypes(excelReportTypes: any): void {
    this.excelReportTypes = [];
    excelReportTypes.forEach(element => {
      // set every excel report type to true in default
      this.excelReportTypes[element] = true;
    });
  }
  // init excel report types table
  private initExcelReportTypes(): void {
    // if donot destroy table, it would not show
    $('#excel-report-types').bootstrapTable('destroy');
    // set headers into table
    const excelReportTypesHeaders: any[] = [];
    // checkbox to set display on excel report types or not
    const that = this;
    const checkboxHeader = {
      align: 'center',
      checkbox: 'true',
      formatter: function (index, row) {
        const ret = {};
        // set checked state
        ret['checked'] = row.value;
        return ret;
      }// end of formatter
    };
    excelReportTypesHeaders.push(checkboxHeader);
    // query conditions display header
    const excelReportTypeHeader = new Header(this.EXCEL_REPORT_TYPES_FIELD, this.EXCEL_REPORT_TYPES_TITLE);
    excelReportTypesHeaders.push(excelReportTypeHeader);
    // load header of table
    $('#excel-report-types').bootstrapTable({
      columns: excelReportTypesHeaders,
      pagination: false,
      height: $(window).height() * 0.5,
      onCheck: function (row: any, $element: any): void {
        const excelReportType = row[that.EXCEL_REPORT_TYPES_FIELD];
        that.excelReportTypes[excelReportType] = true;
      },
      onUncheck: function (row: any, $element: any): void {
        const excelReportType = row[that.EXCEL_REPORT_TYPES_FIELD];
        that.excelReportTypes[excelReportType] = false;
      },
    });
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#excel-report-types').bootstrapTable('resetView', {
        height: $(window).height() * 0.5,
      });
    });
  }
  // convert excel report types to table data
  private convertExcelReportTypesToTableData(): void {
    this.excelReportTypesTableValues = [];
    this.excelReportTypes.forEach(element => {
      const entry = {};
      entry[this.EXCEL_REPORT_TYPES_FIELD] = element;
      entry['value'] = this.excelReportTypes[element];
      this.excelReportTypesTableValues.push(entry);
    });
  }
  // load excel types data into table
  private loadExcelReportTypesIntoTable(): void {
    $('#excel-report-types').bootstrapTable('load', this.excelReportTypes);
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
