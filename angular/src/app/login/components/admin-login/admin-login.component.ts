import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../services/login-service';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css']
})
export class AdminLoginComponent implements OnInit {

  public name: string = null;
  public password: string = null;
  public error = false;
  constructor(private router: Router , private loginService: LoginService) { }
  // constructor(private loginService: LoginService) { }

  ngOnInit() {
  }
  // admin login
  public adminLogin(): void {
    this.loginService.adminLogin(this.name, this.password).subscribe(
      httpResponse => this.adminLoginNotification(httpResponse)
    );
  }

  private adminLoginNotification(httpResponse: HttpResponse) {
    if (httpResponse.code === 200) {
      this.router.navigate(['/web/main']);
    } else {
      this.error = true;
    }
  }
  // on change name or password,clear error
  public onChangeInput(): void {
    this.error = false;
  }

}
