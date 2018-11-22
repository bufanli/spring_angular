import { NgModule, LOCALE_ID } from '@angular/core';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {CommonModule} from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { RootComponent } from './components/root.component';
import { OurCommonModule } from '../common/our-common.module';
import {BrowserModule} from '@angular/platform-browser';

const rootRoutes: Routes = [
    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: 'login',
        loadChildren: '../login/login.module#LoginModule'
    },
    {
        path: 'main',
        loadChildren: '../main/main.module#MainModule'
    }
];

@NgModule({
  declarations: [
    RootComponent,
  ],
  imports: [
    BrowserModule,
    NgbModule.forRoot(),
    CommonModule,
    RouterModule.forRoot(rootRoutes),
    OurCommonModule,
  ],
  bootstrap: [RootComponent]
})
export class RootModule { }
