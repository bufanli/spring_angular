import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DataSearchComponent } from './components/data-search.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

const dataSearchRoutes: Routes = [
    {
        path: '',
        component: DataSearchComponent
    }
];
@NgModule({
  exports: [ RouterModule ],
  declarations: [
    DataSearchComponent,
  ],
  imports: [
    RouterModule.forChild(<any>dataSearchRoutes),
    FormsModule,
    HttpClientModule,
    HttpModule,
  ],

})
export class DataSearchModule { }
