import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { HttpResponse } from 'src/app/common/entities/http-response';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private ADMIN_LOGIN_URL = 'api/loginUserByUsernamePassword';
  constructor(private http: HttpClient, private router: Router) { }

  // admin login
  public adminLogin(name: string, password: string): Observable<HttpResponse> {
    // prepare form data
    const formData = [
      { key: '用户名', value: name },
      { key: '密码', value: password },
    ];
    return this.http.post<HttpResponse>(this.ADMIN_LOGIN_URL, formData, httpOptions);
  }

}
