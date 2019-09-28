import { HttpClient } from '@angular/common/http';
import { AfterViewChecked, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Header } from '../../../common/entities/header';
import { HttpResponse } from '../../../common/entities/http-response';
import { CommonUtilitiesService } from '../../../common/services/common-utilities.service';
import { NgbModal, NgbModalOptions, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { UserEditComponent } from '../user-edit/user-edit.component';
import { UserBasicInfo } from '../../entities/user-basic-info';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { CommonDialogCallback } from 'src/app/common/interfaces/common-dialog-callback';
import { UserInfoService } from '../../services/user-info.service';
const OPERATION_HEADER_INDEX = 11;

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit, AfterViewChecked, CommonDialogCallback {

  private getUsersUrl = 'api/getAllUserBasicInfo';  // URL to get user list
  // this id is just for compiling pass
  private id: string = null;
  private currentPageNumber = 1;
  private isShowLastPage = false;
  // edit user button id's prefix
  private readonly EDIT_USER_PREFIX = 'edit_user_';
  // delete user button id's prefix
  private readonly DELETE_USER_PREFIX = 'delete_user_';
  private readonly DELETE_USER_TITLE = '请确认删除用户';
  private readonly DELETE_USER_BODY = '删除用户姓名:';
  private readonly DELETE_USER_MODAL_TYPE = 'confirmation';
  private readonly DELETE_USER_SOURCE_ID = '001';
  // deleting user id
  private deletingUserID: string = null;
  private readonly DELETE_USER_NG_TITLE = '删除用户失败';
  private readonly DELETE_USER_NG_BODY = '删除失败，用户姓名:';
  private readonly DELETE_USER_MODAL_NG_TYPE = 'warning';
  private readonly DELETE_USER_NG_SOURCE_ID = '002';

  private userListHeaders: Header[] = [
    new Header('userID', 'userID', true),
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
    public modalService: NgbModal,
    private currentUserContainer: CurrentUserContainerService,
    private userInfoService: UserInfoService,
  ) {
  }
  public showLastPage(): void {
    this.isShowLastPage = true;
  }

  ngOnInit() {
    // set headers for user list
    $('#table').bootstrapTable('destroy');
    this.commonUtilitiesService.addTooltipFormatter(this.userListHeaders, 100, 150);
    // add operation formatter to header
    this.addOperationFormatter(this.userListHeaders[OPERATION_HEADER_INDEX]);
    $('#table').bootstrapTable({
      columns: this.userListHeaders,
      pagination: true,
      pageSize: 8,
      height: $(window).height() * 0.8 - 60,
      pageList: [],
    });
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#table').bootstrapTable('resetView', {
        // 60 px is for pagination
        height: $(window).height() * 0.8 - 60,
      });
    });
    // bind user edit handler for every page
    $('#table').on('page-change.bs.table', this, this.bindUserEditEventHandler);
    // get users from server
    this.getUsers().subscribe(httpResponse =>
      this.getUsersNotification(httpResponse));
  }

  /**get users */
  getUsers(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getUsersUrl);
  }

  getUsersNotification(httpResponse: HttpResponse) {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
      return;
    }
    // show user list
    $('#table').bootstrapTable('load', this.commonUtilitiesService.reshapeData(httpResponse.data));
    if (this.isShowLastPage) {
      const options: any = $('#table').bootstrapTable('getOptions');
      this.currentPageNumber = options.totalPages;
      this.isShowLastPage = false;
      $('#table').bootstrapTable('selectPage', this.currentPageNumber);
      this.isShowLastPage = false;
    } else {
      $('#table').bootstrapTable('selectPage', this.currentPageNumber);
    }
  }

  bindUserEditEventHandler(target): void {
    // call from component context
    if (target == null) {
      // get current page data
      const currentPageData = $('#table').bootstrapTable('getData');
      // bind user edit event, this.modalService is passed as target.data
      for (let i = 0; i < currentPageData.length; i++) {
        const editButtonId = '#' + this.EDIT_USER_PREFIX + currentPageData[i]['userID'];
        const deleteButtonId = '#' + this.DELETE_USER_PREFIX + currentPageData[i]['userID'];
        $(editButtonId).on('click', this, this.showUserSettingModal);
        $(deleteButtonId).on('click', this, this.showUserSettingModal);
      }
    } else {
      // call from html context
      const component = target.data;
      // get current page data
      const currentPageData = $('#table').bootstrapTable('getData');
      // bind user edit event, this.modalService is passed as target.data
      for (let i = 0; i < currentPageData.length; i++) {
        const editButtonId = '#' + component.EDIT_USER_PREFIX + currentPageData[i]['userID'];
        const deleteButtonId = '#' + component.DELETE_USER_PREFIX + currentPageData[i]['userID'];
        $(editButtonId).on('click', component, component.showUserSettingModal);
        $(deleteButtonId).on('click', component, component.showDeleteUserModal);
      }
    }
  }
  // set deleting user ID
  public setDeletingUserID(deletingUserID: string): void {
    this.deletingUserID = deletingUserID;
  }
  // show delete user modal
  private showDeleteUserModal(target: any): void {
    const component = target.data;
    const deletingUserID = this.id.substring(component.DELETE_USER_PREFIX.length);
    component.commonUtilitiesService.showCommonDialog(component.DELETE_USER_TITLE,
      component.DELETE_USER_BODY + this.id.substring(component.DELETE_USER_PREFIX.length),
      component.DELETE_USER_MODAL_TYPE,
      component,
      component.DELETE_USER_SOURCE_ID);
    // set deleting user
    component.setDeletingUserID(deletingUserID);
  }
  // user delete confirmation dialog callback
  public callbackOnConfirm(sourceID: string): void {
    if (sourceID === this.DELETE_USER_SOURCE_ID) {
      this.userInfoService.deleteUser(this, this.deletingUserID);
    } else if (sourceID === this.DELETE_USER_NG_SOURCE_ID) {
      // nothing to do
    }
  }

  // add formatter to user list
  addOperationFormatter(operationHeader: Header) {
    operationHeader.formatter = function (value, row, index) {
      const buttonId = row.userID;
      const editButton = '<button type=\'button\' class=\'margin-button btn btn-primary btn-xs\' id=' +
        'edit_user_' + buttonId + ' class=\'btn btn-primary btn-xs \'>\
      <span class=\'glyphicon glyphicon-cog\'></span> 编辑</button>';
      const deleteButton = '<button type=\'button\' class=\'margin-button btn btn-xs\' id=' +
        'delete_user_' + buttonId + ' class=\'btn btn-primary btn-xs \'>\
      <span class=\'glyphicon glyphicon-trash\'></span> 删除</button>';
      return editButton + deleteButton;
    };
  }
  // show tooltip when completing to upload file
  ngAfterViewChecked() {
    $('[data-toggle="tooltip"]').each(function () {
      $(this).tooltip();
    });
  }
  // adjust modal options
  // if don't adjust modal options, modal will not be shown correctly
  adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    options.size = 'lg';
    return options;
  }
  // show modal for user setting
  showUserSettingModal(target): void {
    const service: NgbModal = target.data.modalService;
    const user: UserBasicInfo = target.data.getCurrentUser(this.id.substring(target.data.EDIT_USER_PREFIX.length));
    // you can not call this.adjustModalOptions,
    // because showUserSettingModal called in html context
    const modalRef = service.open(UserEditComponent, target.data.adjustModalOptions());
    modalRef.componentInstance.currentUser = user;
    modalRef.componentInstance.notifyClose.subscribe(response => target.data.callbackOfUserEditEnd(response));
  }

  callbackOfUserEditEnd() {
    const options: any = $('#table').bootstrapTable('getOptions');
    this.currentPageNumber = options.pageNumber;
    // get users from server
    this.getUsers().subscribe(httpResponse =>
      this.getUsersNotification(httpResponse));
  }

  // get current user according to button's id(user's id)
  getCurrentUser(userId: string): UserBasicInfo {
    const user: any = $('#table').bootstrapTable('getRowByUniqueId', userId);
    return user;
  }
  // check session timeout
  public checkSessionTimeout(httpResponse: any): void {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    }
  }
  // on delete user
  public onDeleteUser(httpResponse: HttpResponse): void {
    if (httpResponse.code === 200) {
      // delete ok
      $('#table').bootstrapTable('remove',
        { field: 'userID', values: this.deletingUserID });
    } else {
      // delete ng
      this.commonUtilitiesService.showCommonDialog(
        this.DELETE_USER_NG_TITLE,
        this.DELETE_USER_BODY + this.deletingUserID,
        this.DELETE_USER_MODAL_NG_TYPE,
        this,
        this.DELETE_USER_NG_SOURCE_ID
      );
    }
  }
}
