import { HttpClient } from '@angular/common/http';
import { AfterViewChecked, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Header } from '../../../common/entities/header';
import { HttpResponse } from '../../../common/entities/http-response';
import { CommonUtilitiesService } from '../../../common/services/common-utilities.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UserEditComponent } from '../user-edit/user-edit.component';

const OPERATION_HEADER_INDEX = 11;

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit, AfterViewChecked {

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
    new Header('操作', '操作', true),
  ];
  constructor(private http: HttpClient,
    private commonUtilitiesService: CommonUtilitiesService,
    public modalService: NgbModal) {
  }

  ngOnInit() {
    // set headers for user list
    $('#table').bootstrapTable({ toggle: 'table' });
    $('#table').bootstrapTable('destroy');
    this.commonUtilitiesService.addTooltipFormatter(this.userListHeaders, 50);
    // add operation formatter to header
    this.addOperationFormatter(this.userListHeaders[OPERATION_HEADER_INDEX]);
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
    $('#table').bootstrapTable('load', this.commonUtilitiesService.reshapeData(httpResponse.data));
    // bind user edit event, this.modalService is passed as target.data
    $('#user-edit').on('click', this.modalService, this.showUserSettingModal);
  }

  // add formatter to user list
  addOperationFormatter(operationHeader: Header) {
    operationHeader.formatter = function (value, row, index) {
      return '<button type=\'button\' id=\'user-edit\' class=\'btn btn-primary \'>\
      <span class=\'glyphicon glyphicon-cog\'></span> 设定</button>';
    };
  }
  // show tooltip when completing to upload file
  ngAfterViewChecked() {
    $('[data-toggle="tooltip"]').each(function () {
      $(this).tooltip();
    });
  }
  // show modal for user setting
  showUserSettingModal(target): void {
    const service: NgbModal = target.data;
    service.open(UserEditComponent);
  }
}
