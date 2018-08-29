import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DataSearchComponent } from './data-search/data-search.component';
import {FileUploadComponent} from './file-upload-component/file-upload-component';

const routes: Routes = [
  { path: 'data-search', component: DataSearchComponent},
  { path: 'file-upload', component: FileUploadComponent},
];
@NgModule({
  imports: [
    [ RouterModule.forRoot(routes) ],
  ],
  exports: [ RouterModule ],
  declarations: []
})
export class AppRoutingModule { }
