import { Component, OnInit } from '@angular/core';
import { DataStatisticsService } from '../../services/data-statistics.service';

@Component({
  selector: 'app-data-excel-report-selection',
  templateUrl: './data-excel-report-selection.component.html',
  styleUrls: ['./data-excel-report-selection.component.css']
})
export class DataExcelReportSelectionComponent implements OnInit {
  public readonly SELECT_STATISTICS_REPORT_TYPE = '请选择汇总设定';
  public readonly PRODUCT_CODE = '请选择商品编码';
  public readonly REPORT_MONTH = '请选择报告月份';
  public readonly EXPORT_OR_NOT = '请选择进出口';
  public readonly EXPORT = '出口';
  public readonly IMPORT = '进口';
  public readonly CLOSE_BUTTON = '关闭';
  public readonly EXPORT_EXCEL_REPORT = '导出汇总报告';
  // model data
  public selectedProductCode = '';
  public selectedReportMonth = '';
  public selectedExportOrNot = '';
  constructor(private dataStatisticsService: DataStatisticsService) { }

  ngOnInit() {
  }
  // excel report
  public excelReport(): void {
    this.dataStatisticsService.excelReportSetting();
  }
  // close dialogue
  public close(): void {

  }

}
