import { Component, OnInit , AfterViewChecked} from '@angular/core';
import { Header } from '../../../common/entities/header';
import { HttpResponse } from '../../../common/entities/http-response';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { CommonUtilitiesService } from '../../../common/services/common-utilities.service';

const header = new HttpHeaders({ 'Content-Type': 'application/json' });

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit , AfterViewChecked {

  private getUsersUrl = 'getUsers';  // URL to get user list

  private userListHeaders: Header[] = [
    new Header('userID', 'userID', false),
    new Header('昵称', '昵称', true),
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
  constructor(private http: HttpClient,
              private commonUtilitiesService: CommonUtilitiesService) {
  }

  ngOnInit() {
    // set headers for user list
    $('#table').bootstrapTable({ toggle: 'table' });
    $('#table').bootstrapTable('destroy');
    this.commonUtilitiesService.addTooltipFormatter(this.userListHeaders, 50);
    $('#table').bootstrapTable({ columns: this.userListHeaders });
    // get users from server
    this.getUsers().subscribe(httpResponse =>
      this.getUsersNotification(httpResponse));
  }

  /**get users */
  getUsers(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getUsersUrl);
  }

  getUsersNotification(httpResponse: HttpResponse) {
    // show user list
    console.log(httpResponse.data['电子邮件']);
    $('#table').bootstrapTable('load', this.commonUtilitiesService.reshapeData(httpResponse.data));
  }

  // add formatter to user list
  addFormatterToHeaders() {

  }
  // show tooltip when completing to upload file
  ngAfterViewChecked() {
    $('[data-toggle="tooltip"]').each(function () {
      $(this).tooltip();
    });
  }
}
