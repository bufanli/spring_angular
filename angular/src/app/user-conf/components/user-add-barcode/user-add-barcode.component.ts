import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { HttpClient } from '@angular/common/http';
declare var WxLogin: any;

@Component({
  selector: 'app-user-add-barcode',
  templateUrl: './user-add-barcode.component.html',
  styleUrls: ['./user-add-barcode.component.css']
})
export class UserAddBarcodeComponent implements OnInit {

  private userAddURL = 'weChatCallbackForAddUser?code=123456&state=20181222';  // URL to login(dummy)
  public isUserRefused = false;
  public isUserExisted = false;

  constructor(private http: HttpClient) { }
  ngOnInit() {
    // show bar code to add user, now it is dummy
    this.showWechatBarCode();
  }
  // show bar code to add user todo
  private showWechatBarCode() {
    const obj = new WxLogin({
      self_redirect: false,
      id: 'bar-code',
      appid: 'wx84fe1c3116fb46fa',
      scope: 'snsapi_login',
      redirect_uri: 'http://www.sinoshuju.com/weChatCallbackForAddUser',
      state: '20181226',
      style: '',
      href: '',
    });
  }
  // dummy scan bar code
  public dummyScanBarCode() {
    window.location.href = this.userAddURL;
  }
  // set wechat failed reason
  public setReason(reason: string) {
    if (reason === 'user_exist') {
      this.isUserExisted = true;
      this.isUserRefused = false;
    } else if (reason === 'user_refused') {
      this.isUserExisted = false;
      this.isUserRefused = true;
    } else {
      // nothing to do
    }
  }

}
