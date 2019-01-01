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
import { UserBasicInfoComponent } from './components/user-basic-info/user-basic-info.component';
import { UserAccessAuthoritiesComponent } from './components/user-access-authorities/user-access-authorities.component';
import { UserAddInputComponent } from './components/user-add-input/user-add-input.component';
import { UserAddBarcodeComponent } from './components/user-add-barcode/user-add-barcode.component';

const userConfRoutes: Routes = [
  {
    path: '',
    component: UserConfComponent,
  },
  {
    path: ':action',
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
  declarations: [
    UserListComponent,
    UserAddComponent,
    UserConfComponent,
    UserEditComponent,
    UserBasicInfoComponent,
    UserAccessAuthoritiesComponent,
    UserAddInputComponent,
    UserAddBarcodeComponent,
  ],
  entryComponents: [
    UserEditComponent,
    UserBasicInfoComponent,
    UserAccessAuthoritiesComponent,
    UserAddBarcodeComponent,
    UserAddInputComponent,
  ],
})
export class UserConfModule { }
