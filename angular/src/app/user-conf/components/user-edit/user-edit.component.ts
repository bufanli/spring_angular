import { Component, OnInit, ViewChild, ViewContainerRef, ComponentRef, OnDestroy, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { UserAccessAuthoritiesComponent } from '../user-access-authorities/user-access-authorities.component';
import { UserBasicInfoComponent } from '../user-basic-info/user-basic-info.component';
import { UserInfoService } from '../../services/user-info.service';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { UserBasicInfo } from '../../entities/user-basic-info';
import { UserQueryConditionHeader } from '../../entities/user-query-condition-header';
import { ComponentFactoryResolver, ComponentFactory } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { CommonDialogCallback } from 'src/app/common/interfaces/common-dialog-callback';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { UserQueryConditionsComponent } from '../user-query-conditions/user-query-conditions.component';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit, OnDestroy, CommonDialogCallback {

  private static readonly USER_EDIT_ERROR_TITLE = '用户更新失败';
  private static readonly USER_EDIT_ERROR_SOURCE_ID = 'SOURCE_ID_ERROR';
  private static readonly USER_EDIT_ERROR_TYPE = 'danger';

  // user access authorities
  public currentUserAccessAuthorities: UserAccessAuthorities = null;
  // user header display
  // TODO this will be replace to entity class in future from any type
  public currentUserHeaderDisplay: UserQueryConditionHeader = null;
  // user query condition display
  public currentUserQueryConditionDisplay: UserQueryConditionHeader = null;
  // user basic info
  public currentUser: UserBasicInfo = null;

  @ViewChild('userEditContainer', { read: ViewContainerRef }) container: ViewContainerRef;
  componentRefBasicInfo: ComponentRef<UserBasicInfoComponent>;
  componentRefAccessAuthorities: ComponentRef<UserAccessAuthoritiesComponent>;
  componentRefQueryConditionDisplays: ComponentRef<UserQueryConditionsComponent>;

  @Output() notifyClose: EventEmitter<string> = new EventEmitter<string>();

  constructor(private activeModal: NgbActiveModal,
    private userInfoService: UserInfoService,
    private resolver: ComponentFactoryResolver,
    private commonUtilitiesService: CommonUtilitiesService,
    private currentUserContainer: CurrentUserContainerService) {
  }

  createComponent(type: string) {
    this.container.clear();
    if (type === 'basic-info') {
      const factory = this.resolver.resolveComponentFactory(UserBasicInfoComponent);
      this.componentRefBasicInfo = this.container.createComponent(factory);
      this.componentRefBasicInfo.instance.currentUser = this.currentUser;
    } else if (type === 'access-authorities') {
      const factory = this.resolver.resolveComponentFactory(UserAccessAuthoritiesComponent);
      this.componentRefAccessAuthorities = this.container.createComponent(factory);
      this.componentRefAccessAuthorities.instance.setCurrentUserAccessAuthorities(this.currentUserAccessAuthorities);
    } else if (type === 'query-condition-displays') {
      const factory = this.resolver.resolveComponentFactory(UserQueryConditionsComponent);
      this.componentRefQueryConditionDisplays = this.container.createComponent(factory);
      this.componentRefQueryConditionDisplays.instance.setQueryConditionDisplays(this.currentUserQueryConditionDisplay);
      this.componentRefQueryConditionDisplays.instance.setUserAccessAuthorities(this.currentUserAccessAuthorities);
    }

  }
  ngOnInit() {
    this.userInfoService.getUserDetailedInfo(
      this,
      this.currentUser['userID']);
    this.createComponent('basic-info');
  }

  close() {
    this.activeModal.close();
  }

  // set user permission after user edit component getting user permission
  public setUserAccessAuthorities(userAccessAuthorities: UserAccessAuthorities) {
    this.currentUserAccessAuthorities = userAccessAuthorities;
  }

  public setUserHeaderDisplay(headerDisplay: any) {
    this.currentUserHeaderDisplay = headerDisplay;
  }
  public setUserQueryConditionDisplay(queryConditionDisplay: any) {
    this.currentUserQueryConditionDisplay = queryConditionDisplay;
  }
  // update user info(basic info, permissions, query contiditons, headers)
  public updateUserInfo() {
    // basic info
    let basicInfo: UserBasicInfo = null;
    if (this.componentRefBasicInfo !== undefined) {
      basicInfo = this.componentRefBasicInfo.instance.getCurrentUserBasicInfo();
    } else {
      basicInfo = this.currentUser;
    }
    // access authorities
    let accessAuthorities: UserAccessAuthorities = null;
    if (this.componentRefAccessAuthorities !== undefined) {
      accessAuthorities = this.componentRefAccessAuthorities.instance.
        getCurrentUserAccessAuthorities();
    } else {
      accessAuthorities = this.currentUserAccessAuthorities;
    }
    let queryConditionDisplays: any = null;
    // query condition displays
    if (this.componentRefQueryConditionDisplays !== undefined) {
      queryConditionDisplays = this.componentRefQueryConditionDisplays
        .instance.getQueryCondtionDisplays();
    } else {
      queryConditionDisplays = this.currentUserQueryConditionDisplay;
    }
    // call service
    this.userInfoService.updateUserInfo(
      this,
      basicInfo,
      accessAuthorities,
      this.currentUserHeaderDisplay,
      queryConditionDisplays);
  }
  public updateUserInfoCallback(httpResponse: HttpResponse): void {
    if (httpResponse.code === 200) {
      this.notifyClose.emit('closed');
      // update ok
      this.activeModal.close();
    } else if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
      // close user edit user modal dialog
      this.activeModal.close();
    } else {
      // get message from response when add user failed
      const failedReason = httpResponse.message;
      // show modal dialog of error
      this.commonUtilitiesService.showCommonDialog(
        UserEditComponent.USER_EDIT_ERROR_TITLE,
        failedReason,
        UserEditComponent.USER_EDIT_ERROR_TYPE,
        this,
        UserEditComponent.USER_EDIT_ERROR_SOURCE_ID);
    }

  }
  ngOnDestroy() {
    if (this.componentRefBasicInfo != null) {
      this.componentRefBasicInfo.destroy();
    }
    if (this.componentRefAccessAuthorities != null) {
      this.componentRefAccessAuthorities.destroy();
    }
  }
  // callback on modal dialog closed
  callbackOnConfirm(sourceID: string): void {
    // if error modal dialog is closed, then do nothing
    if (sourceID === UserEditComponent.USER_EDIT_ERROR_SOURCE_ID) {
      // nothing to do
    } else {
      // it is impossible, so do nothing
    }
  }
  public checkSessionTimeout(httpResponse: any): void {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    }
  }
}
