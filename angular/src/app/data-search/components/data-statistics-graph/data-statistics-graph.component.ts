import { Component, OnInit } from '@angular/core';
import { StatisticsReportEntry } from '../../entities/statistics-report-entry';

@Component({
  selector: 'app-data-statistics-graph',
  templateUrl: './data-statistics-graph.component.html',
  styleUrls: ['./data-statistics-graph.component.css']
})
export class DataStatisticsGraphComponent implements OnInit {
  // graph options for statistics graph
  public options: any;
  // statistics report entries
  private statisticsReportEntries: StatisticsReportEntry[] = null;
  // statistics chart compute field
  private chartComputeField: string = null;
  // statistics service
  private dataStatisticsService: any = null;
  // group by field
  private groupByField: string = null;
  constructor() { }

  ngOnInit() {
    // convert statistics report entries to options
    this.options = this.dataStatisticsService.
      convertStatisticsReportToOptions(
        this.statisticsReportEntries,
        this.groupByField,
        this.chartComputeField);
    // register resize function to echarts
    $('#echarts').height($(window).height() * 0.45);
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#echarts').height($(window).height() * 0.45);
    });
  }
  // set statistics report entries
  public setStatisticsReportEntries(statisticsReportEntries: StatisticsReportEntry[]): void {
    this.statisticsReportEntries = statisticsReportEntries;
  }
  // set chart compute field
  public setChartComputeField(chartComputeField: string): void {
    this.chartComputeField = chartComputeField;
  }
  // set statistics service
  public setDataStatisticsService(dataStatisticsService: any): void {
    this.dataStatisticsService = dataStatisticsService;
  }
  // set group by field
  public setGroupByField(groupByField: string): void {
    this.groupByField = groupByField;
  }

}
