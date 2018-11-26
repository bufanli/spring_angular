import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InMemoryDataService } from './services/in-memory-data.service';
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';

@NgModule({
  exports: [
    RouterModule,
  ],
  declarations: [
  ],
  imports: [
            // when running with a real server, then comment out followoing codes
            // HttpClientInMemoryWebApiModule.forRoot(
            //   InMemoryDataService, { dataEncapsulation: false }
            // ),
  ],
})
export class OurCommonModule { }
