import { Component, OnInit } from '@angular/core';
import { Header } from '../../../common/entities/header';
import { HttpResponse } from '../../../common/entities/http-response';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';

const header = new HttpHeaders({ 'Content-Type': 'application/json' });

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

  private getUsersUrl = 'getUsers';  // URL to get user list

  private userListHeaders: Header[] = [
    new Header('userID', 'userID', false),
    new Header('用户昵称', '用户昵称', true),
    new Header('性别', '性别', true),
    new Header('名字', '名字', true),
    new Header('密码', '密码', true),
    new Header('国家', '国家', true),
    new Header('省份', '省市', true),
    new Header('城市', '城市', true),
    new Header('地址', '地址', true),
    new Header('手机号码', '手机号码', true),
    new Header('电子邮件', '电子邮件', true),
    new Header('权限设置', '权限设置', true),
  ];
  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    // set headers for user list
    $('#table').bootstrapTable({ toggle: 'table' });
    $('#table').bootstrapTable('destroy');
    $('#table').bootstrapTable({ columns: this.userListHeaders });
  }

  /**get users */
  getUsers(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getUsersUrl);
  }

  getUsersNotification(httpResponse: HttpResponse) {
    $('#table').bootstrapTable('destroy');
    // show user list
    $('#table').bootstrapTable('load', httpResponse.data);
  }

  // add formatter to user list
  addFormatterToHeaders() {

  }
}
