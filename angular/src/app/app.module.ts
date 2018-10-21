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
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService } from './data-service/in-memory-data.service';
import { HttpClientModule } from '@angular/common/http';

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
    HttpClientModule,
    // when running with a real server, then comment out followoing codes
    // HttpClientInMemoryWebApiModule.forRoot(
    //   InMemoryDataService, { dataEncapsulation: false }
    // ),
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'zh-CN' },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
