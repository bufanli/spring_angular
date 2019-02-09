import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DataSearchComponent } from './components/data-search/data-search.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { DataDetailComponent } from './components/data-detail/data-detail.component';
import { CommonModule } from '@angular/common';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { DataStatisticsComponent } from './components/data-statistics/data-statistics.component';
import { NgxEchartsModule } from 'ngx-echarts';

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
  ],
  imports: [
    RouterModule.forChild(<any>dataSearchRoutes),
    FormsModule,
    CommonModule,
    HttpClientModule,
    HttpModule,
    NgbModalModule,
    NgxEchartsModule,
  ],
  entryComponents: [
    DataDetailComponent,
    DataStatisticsComponent,
  ]

})
export class DataSearchModule { }
