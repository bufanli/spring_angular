import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { Http } from '@angular/http';
declare var WxLogin: any;

// json header for get
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'text/html' })
};
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, AfterViewChecked {

  private loginURL = 'weChatCallbackForLogin?code=123456&state=20181224';  // URL to login(dummy)
  public isUserNotInDB = false;
  public isUserRefused = false;
  constructor(private router: Router,
    private http: HttpClient,
    private otherHttp: Http,
    private activatedRoute: ActivatedRoute,
    private currentUserContainerService: CurrentUserContainerService) {
    this.isUserNotInDB = false;
    this.isUserRefused = false;
  }

  ngAfterViewChecked() {
    this.activatedRoute.queryParams.subscribe((params: Params) => {
      const auth = params['auth'];
      console.log('auth is ' + auth);
    });
  }
  ngOnInit() {
    // show bar code
    this.showBarCode();
    this.activatedRoute.queryParams.subscribe((params: Params) => {
      const auth = params['auth'];
      if (auth === 'ok') {
        // get open id
        const openID = params['openid'];
        // save it into current user container service
        this.currentUserContainerService.saveUserOpenID(this, openID);
      } else if (auth === 'ng') {
        const reason = params['reason'];
        if (reason === 'user_refused') {
          this.isUserRefused = true;
          this.isUserNotInDB = false;
        } else {
          this.isUserRefused = false;
          this.isUserNotInDB = true;
        }
      } else {
        // nothing to do
      }
    });
  }

  // if wechat is ok, this dummy method will be deleted
  public dummyLogin(): void {
    window.location.href = this.loginURL;
  }
  // call back to finish getting user basic info and detail info
  public getUserInfoCallback(errorCode: number) {
    if (errorCode === 0) {
      this.router.navigate(['/web/main']);
    } else {
      // todo
    }
  }
  private showBarCode() {
    const obj = new WxLogin({
      self_redirect: true,
      id: 'bar-code',
      appid: 'wx84fe1c3116fb46fa',
      scope: 'snsapi_login',
      redirect_uri: 'http://www.sinoshuju.com/weChatCallbackForLogin',
      state: '20181226',
      style: '',
      href: '',
    });
  }
}
