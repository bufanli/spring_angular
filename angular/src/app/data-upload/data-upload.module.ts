import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DataUploadComponent } from './components/data-upload/data-upload.component';
import { FormsModule } from '@angular/forms';
import { FileUploadModule } from 'ng2-file-upload';
import { CommonModule } from '@angular/common';
import { DataUploadConfComponent } from './components/data-upload-conf/data-upload-conf.component';
import { EditDictionaryComponent } from './components/edit-dictionary/edit-dictionary.component';
import { EditSynonymComponent } from './components/edit-synonym/edit-synonym.component';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';

const dataUploadRoutes: Routes = [
  {
    path: '',
    component: DataUploadConfComponent
  }
];
@NgModule({
  imports: [
    RouterModule.forChild(<any>dataUploadRoutes),
    FormsModule,
    FileUploadModule,
    CommonModule,
    NgbModalModule,
  ],
  exports: [RouterModule],
  declarations: [
    DataUploadComponent,
    DataUploadConfComponent,
    EditDictionaryComponent,
    EditSynonymComponent,
  ],
  entryComponents: [
    EditSynonymComponent,
  ],
})
export class DataUploadModule { }
