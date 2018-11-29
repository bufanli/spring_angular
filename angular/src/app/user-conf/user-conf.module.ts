import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserListComponent } from './components/user-list/user-list.component';
import { UserAddComponent } from './components/user-add/user-add.component';
import { UserConfComponent } from './components/user-conf/user-conf.component';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { UserEditComponent } from './components/user-edit/user-edit.component';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';

const userConfRoutes: Routes = [
  {
    path: '',
    component: UserConfComponent,
  },
];
@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    NgbModalModule,
    FormsModule,
    RouterModule.forChild(userConfRoutes),
  ],
  declarations: [UserListComponent, UserAddComponent, UserConfComponent, UserEditComponent],
  entryComponents: [UserEditComponent],
})
export class UserConfModule { }
