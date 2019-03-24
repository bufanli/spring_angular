import { Component, OnInit } from '@angular/core';
import { StatisticsReportEntry } from '../../entities/statistics-report-entry';
import { DataStatisticsService } from '../../services/data-statistics.service';
import { Header } from 'src/app/common/entities/header';

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
  constructor(private statisticsService: DataStatisticsService) { }

  ngOnInit() {
    // get headers from selected group by field
    // and selected compute fields
    let statisticsHeadersOrigin: string[] = [];
    statisticsHeadersOrigin = statisticsHeadersOrigin.concat(this.selectedGroupbyField);
    statisticsHeadersOrigin = statisticsHeadersOrigin.concat(this.selectedComputeFields);
    const statisticsHeaders: Header[] = [];
    statisticsHeadersOrigin.forEach(element => {
      const tempHeader = new Header(element, element);
      statisticsHeaders.push(tempHeader);
    });
    // load header of table
    $('#table').bootstrapTable({
      columns: statisticsHeaders,
      pagination: true,
      pageSize: this.statisticsService.TOP_N,
      pageList: [],
    });
    // load data into table

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
