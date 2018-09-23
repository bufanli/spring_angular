import { BrowserModule } from '@angular/platform-browser';
import { NgModule, LOCALE_ID } from '@angular/core';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {CommonModule} from '@angular/common';
import {FileUploadModule} from 'ng2-file-upload';

import { AppComponent } from './app.component';
import { FileUploadComponent} from './file-upload-component/file-upload-component';
import { DataSearchComponent } from './data-search/data-search.component';
import { AppRoutingModule } from './/app-routing.module';
import { FormsModule} from '@angular/forms';

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
    AppRoutingModule,
    FormsModule,
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'zh-CN' },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
