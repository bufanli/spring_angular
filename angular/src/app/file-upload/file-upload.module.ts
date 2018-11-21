import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FileUploadComponent } from './components/file-upload.component';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

const fileUploadRoutes = [
    {
        path: '',
        component: FileUploadComponent
    }
];
@NgModule({
  imports: [
    RouterModule.forChild(<any>fileUploadRoutes),
    FormsModule,
    BrowserModule,
  ],
  exports: [ RouterModule ],
  declarations: [
    FormsModule,
    FileUploadComponent,
  ],
  bootstrap: [FileUploadComponent],
})
export class FileUploadModule { }
