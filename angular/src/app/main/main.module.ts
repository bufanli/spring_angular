import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from './components/main.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

const mainRoutes: Routes = [
    {
        path: '',
        redirectTo: 'data-search',
        pathMatch: 'full'
    },
    {
        path: 'data-search',
        component: MainComponent,
        loadChildren: '../data-search/data-search.module#DataSearchModule'
    },
    {
        path: 'data-upload',
        component: MainComponent,
        loadChildren: '../data-upload/data-upload.module#DataUploadModule'
    },
    {
        path: 'user-conf',
        component: MainComponent,
        loadChildren: '../user-conf/user-conf.module#UserConfModule'
    }
];
@NgModule({
  imports: [
    RouterModule.forChild(mainRoutes),
    FormsModule,
    CommonModule,
  ],
  exports: [ RouterModule ],
  declarations: [
    MainComponent,
  ],
})
export class MainModule { }
