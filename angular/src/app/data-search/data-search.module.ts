import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DataSearchComponent } from './components/data-search.component';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

const dataSearchRoutes = [
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
    BrowserModule,
  ],

})
export class DataSearchModule { }
