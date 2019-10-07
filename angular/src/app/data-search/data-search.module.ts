import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DataSearchComponent } from './components/data-search/data-search.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { DataDetailComponent } from './components/data-detail/data-detail.component';
import { CommonModule } from '@angular/common';
import { NgbModalModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataStatisticsComponent } from './components/data-statistics/data-statistics.component';
import { NgxEchartsModule } from 'ngx-echarts';
import { DataStatisticsService } from './services/data-statistics.service';
import { CommonUtilitiesService } from '../common/services/common-utilities.service';
import { DataStatisticsGraphComponent } from './components/data-statistics-graph/data-statistics-graph.component';
import { DataStatisticsOriginalDataComponent } from './components/data-statistics-original-data/data-statistics-original-data.component';
import { DataExcelReportSelectionComponent } from './components/data-excel-report-selection/data-excel-report-selection.component';
import { ExcelReportService } from './services/excel-report.service';

const dataSearchRoutes: Routes = [
  {
    path: '',
    component: DataSearchComponent
  }
];
@NgModule({
  exports: [RouterModule],
  declarations: [
    DataSearchComponent,
    DataDetailComponent,
    DataStatisticsComponent,
    DataStatisticsGraphComponent,
    DataStatisticsOriginalDataComponent,
    DataExcelReportSelectionComponent,
  ],
  imports: [
    RouterModule.forChild(<any>dataSearchRoutes),
    FormsModule,
    CommonModule,
    HttpClientModule,
    // tslint:disable-next-line: deprecation
    HttpModule,
    NgbModalModule,
    NgxEchartsModule,
  ],
  entryComponents: [
    DataDetailComponent,
    DataStatisticsComponent,
    DataStatisticsGraphComponent,
    DataStatisticsOriginalDataComponent,
    DataExcelReportSelectionComponent,
  ],
  providers: [
    DataStatisticsService,
    ExcelReportService,
  ]
})
export class DataSearchModule { }
