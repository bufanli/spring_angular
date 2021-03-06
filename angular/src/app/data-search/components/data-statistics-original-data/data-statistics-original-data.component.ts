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
  // data statistics service
  private dataStatisticsService: DataStatisticsService = null;
  constructor() { }

  ngOnInit() {
    // if donot destroy table, it would not show
    $('#statistics-table').bootstrapTable('destroy');
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
    $('#statistics-table').bootstrapTable({
      columns: statisticsHeaders,
      pagination: false,
      pageSize: this.dataStatisticsService.TOP_N,
      pageList: [],
      height: $(window).height() * 0.5 - 60,
    });
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#statistics-table').bootstrapTable('resetView', {
        // 60 px is for pagination
        height: $(window).height() * 0.5 - 60,
      });
    });
    // convert statistics report entries into data
    const statisticsData =
      this.convertAllStatisticsReportEntriesIntoData();
    // load data into table
    $('#statistics-table').bootstrapTable('load', statisticsData);
  }
  // set statistics report entries
  public setStatisticsReportEntries(statisticsReportEntries: StatisticsReportEntry[]): void {
    this.statisticsReportEntries = statisticsReportEntries;
  }
  // set selected group by field
  public setSelectedGroupbyField(selectedGroupbyField: string): void {
    this.selectedGroupbyField = selectedGroupbyField;
  }
  // set selected compute fields
  public setSelectedComputeFields(selectedComputeFields: string[]): void {
    this.selectedComputeFields = selectedComputeFields;
  }
  // convert all statistics report entries into data
  private convertAllStatisticsReportEntriesIntoData(): any {
    const statisticsData = [];
    this.statisticsReportEntries.forEach(element => {
      statisticsData.push(this.convertOneStatisticsReportEntryIntoData(element));
    });
    return statisticsData;
  }
  // convert one statistics report entry into data
  private convertOneStatisticsReportEntryIntoData(statisticsReportEntry: StatisticsReportEntry): any {
    const oneStatisticsData: any = {};
    // group by field
    oneStatisticsData[this.selectedGroupbyField] = statisticsReportEntry.getGroupByField();
    // repeat each compute values
    statisticsReportEntry.getComputeValues().forEach(element => {
      oneStatisticsData[element.getComputeField()] = element.getComputeValue();
    });
    return oneStatisticsData;
  }
  // set statistics service
  public setDataStatisticsService(dataStatisticsService: any): void {
    this.dataStatisticsService = dataStatisticsService;
  }
}
