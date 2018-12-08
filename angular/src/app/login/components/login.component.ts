import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  private loginURL = 'dummyLogin';  // URL to login(dummy)
  constructor(private http: HttpClient) { }

  ngOnInit() {
  }

  // if wechat is ok, this dummy method will be deleted
  public dummyLogin(): void {
    this.dummyLoginImpl().subscribe(
      httpResponse => this.dummyLoginNotification(httpResponse));

  }
  private dummyLoginImpl(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.loginURL);
  }
  private dummyLoginNotification(httpResponse: HttpResponse) {

  }
}
