import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from './components/main.component';

const mainRoutes: Routes = [
    {
        path: '',
        redirectTo: 'data-search',
        pathMatch: 'full'
    },
    {
        path: 'data-search',
        loadChildren: '../data-search/data-search.module#DataSearchModule'
    },
    {
        path: 'file-upload',
        loadChildren: '../data-upload/data-upload.module#DataUploadModule'
    }
];
@NgModule({
  imports: [
    RouterModule.forChild(mainRoutes),
  ],
  exports: [ RouterModule ],
  declarations: [
     MainComponent
  ]
})
export class MainModule { }
