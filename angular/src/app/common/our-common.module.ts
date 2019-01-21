import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InMemoryDataService } from './services/in-memory-data.service';
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { ModalDialogComponent } from './components/modal-dialog/modal-dialog.component';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';

@NgModule({
  exports: [
    RouterModule,
  ],
  declarations: [
    ModalDialogComponent,
  ],
  imports: [
    NgbModalModule,
    FormsModule,
    CommonModule,
    BrowserModule,
    RouterModule,
    // when running with a real server, then comment out followoing codes
    // HttpClientInMemoryWebApiModule.forRoot(
    //   InMemoryDataService, { dataEncapsulation: false }
    // ),
  ],
  entryComponents: [
    ModalDialogComponent,
  ]
})
export class OurCommonModule { }
