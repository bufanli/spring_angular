import { NgModule, LOCALE_ID } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { RootComponent } from './components/root.component';
import { OurCommonModule } from '../common/our-common.module';
import { BrowserModule } from '@angular/platform-browser';
import { InMemoryDataService } from '../common/services/in-memory-data.service';
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
import { MainGuardService } from '../login/services/main-guard.service';

const rootRoutes: Routes = [
  {
    path: '',
    redirectTo: 'web/login',
    pathMatch: 'full'
  },
  {
    path: 'web/login',
    loadChildren: '../login/login.module#LoginModule'
  },
  {
    path: 'web/main',
    loadChildren: '../main/main.module#MainModule',
    canActivate: [MainGuardService],
  }
];

@NgModule({
  declarations: [
    RootComponent,
  ],
  providers: [
    MainGuardService,
  ],
  imports: [
    BrowserModule,
// tslint:disable-next-line: deprecation
    NgbModule.forRoot(),
    CommonModule,
    RouterModule.forRoot(rootRoutes),
    OurCommonModule,
// tslint:disable-next-line: deprecation
    HttpModule,
    HttpClientModule,
    // HttpClientInMemoryWebApiModule.forRoot(
    //   InMemoryDataService, { dataEncapsulation: false }
    // ),
  ],
  bootstrap: [RootComponent]
})
export class RootModule { }
