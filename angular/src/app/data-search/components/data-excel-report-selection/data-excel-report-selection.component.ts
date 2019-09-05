import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-data-excel-report-selection',
  templateUrl: './data-excel-report-selection.component.html',
  styleUrls: ['./data-excel-report-selection.component.css']
})
export class DataExcelReportSelectionComponent implements OnInit {
  public readonly SELECT_STATISTICS_REPORT_TYPE = '请选择汇总类型';
  constructor() { }

  ngOnInit() {
  }
  // excel report
  public excelReport(): void {

  }
  // close dialogue
  public close(): void {

  }

}
