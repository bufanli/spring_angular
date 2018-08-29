import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {CommonModule} from '@angular/common';
import {FileUploadModule} from 'ng2-file-upload';

import { AppComponent } from './app.component';
import { FileUploadComponent} from './file-upload-component/file-upload-component';
import { DataSearchComponent } from './data-search/data-search.component';
import { AppRoutingModule } from './/app-routing.module';

@NgModule({
  declarations: [
    AppComponent,
    FileUploadComponent,
    DataSearchComponent,
  ],
  imports: [
    BrowserModule,
    NgbModule.forRoot(),
    CommonModule,
    FileUploadModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
