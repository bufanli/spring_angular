import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login.component';
import { HttpClientModule } from '@angular/common/http';

export const loginRoutes: Routes = [
  {
    path: '',
    component: LoginComponent
  }
];
@NgModule({
  imports: [
    RouterModule.forChild(<any>loginRoutes),
    HttpClientModule,
  ],
  exports: [RouterModule],
  declarations: [
    LoginComponent,
  ]
})
export class LoginModule { }
