import { Component, OnInit } from '@angular/core';
import { StatisticsReportEntry } from '../../entities/statistics-report-entry';

@Component({
  selector: 'app-data-statistics-original-data',
  templateUrl: './data-statistics-original-data.component.html',
  styleUrls: ['./data-statistics-original-data.component.css']
})
export class DataStatisticsOriginalDataComponent implements OnInit {

  // statistics report entries
  private statisticsReportEntries: StatisticsReportEntry[] = null;
  // selected group by field
  private selectedGroupbyField: string = null;
  // selected compute fields
  private selectedComputeFields: string[] = null;
  constructor() { }

  ngOnInit() {
  }
  // set statistics report entries
  public setStatisticsReportEntries(statisticsReportEntries: StatisticsReportEntry[]): void {
    this.statisticsReportEntries = statisticsReportEntries;
  }
  // set selected group by field
  public setSelectedGroupbyField(selectedGroupbyField): void {
    this.selectedGroupbyField = selectedGroupbyField;
  }
  // set selected compute fields
  public setSelectedComputeFields(selectedComputeFields: string[]): void {
    this.selectedComputeFields = selectedComputeFields;
  }
}
