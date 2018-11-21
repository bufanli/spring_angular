import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from './components/main.component';

const mainRoutes = [
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
        loadChildren: '../file-upload/file-upload.module#FileUploadModule'
    }
];
@NgModule({
  imports: [
    RouterModule.forRoot(mainRoutes),
  ],
  exports: [ RouterModule ],
  declarations: [
     MainComponent
  ]
})
export class MainModule { }
