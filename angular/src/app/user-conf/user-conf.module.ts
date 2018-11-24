import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserListComponent } from './components/user-list/user-list.component';
import { UserAddComponent } from './components/user-add/user-add.component';
import { UserConfComponent } from './components/user-conf/user-conf.component';
import { RouterModule, Routes } from '@angular/router';

const userConfRoutes: Routes = [
  {
    path: '',
    component: UserConfComponent,
  },
];
@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(userConfRoutes),
  ],
  declarations: [UserListComponent, UserAddComponent, UserConfComponent]
})
export class UserConfModule { }
