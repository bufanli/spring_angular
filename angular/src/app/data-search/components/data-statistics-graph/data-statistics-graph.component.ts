import { Component, OnInit } from '@angular/core';
import { StatisticsReportEntry } from '../../entities/statistics-report-entry';
import { DataStatisticsService } from '../../services/data-statistics.service';

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
  constructor(private dataStatisticsService: DataStatisticsService) { }

  ngOnInit() {
    // convert statistics report entries to options
    this.options = this.dataStatisticsService.
      convertStatisticsReportToOptions(this.statisticsReportEntries,
        this.chartComputeField);
  }
  // set statistics report entries
  public setStatisticsReportEntries(statisticsReportEntries: StatisticsReportEntry[]): void {
    this.statisticsReportEntries = statisticsReportEntries;
  }
  // set chart compute field
  public setChartComputeField(chartComputeField: string): void {
    this.chartComputeField = chartComputeField;
  }

}
