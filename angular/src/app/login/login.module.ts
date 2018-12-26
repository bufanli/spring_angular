import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HttpClientModule } from '@angular/common/http';
import { AdminLoginComponent } from './components/admin-login/admin-login.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

export const loginRoutes: Routes = [
  {
    path: '',
    redirectTo: 'user',
    pathMatch: 'full',
  },
  {
    path: 'user',
    component: LoginComponent
  },
  {
    path: 'admin',
    component: AdminLoginComponent,
  }
];
@NgModule({
  imports: [
    RouterModule.forChild(<any>loginRoutes),
    HttpClientModule,
    FormsModule,
    CommonModule,
  ],
  exports: [RouterModule],
  declarations: [
    LoginComponent,
    AdminLoginComponent,
  ]
})
export class LoginModule { }
