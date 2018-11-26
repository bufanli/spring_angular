import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DataUploadComponent} from './components/data-upload.component';
import { FormsModule } from '@angular/forms';
import { FileUploadModule } from 'ng2-file-upload';
import { CommonModule } from '@angular/common';

const dataUploadRoutes: Routes = [
    {
        path: '',
        component: DataUploadComponent
    }
];
@NgModule({
  imports: [
    RouterModule.forChild(<any>dataUploadRoutes),
    FormsModule,
    FileUploadModule,
    CommonModule,
  ],
  exports: [ RouterModule ],
  declarations: [
    DataUploadComponent,
  ],
})
export class DataUploadModule { }
