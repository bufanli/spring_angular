import { Component, OnInit, ViewContainerRef, ViewChild, OnDestroy, ComponentFactoryResolver, ComponentRef } from '@angular/core';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { UserQueryConditionHeader } from '../../entities/user-query-condition-header';
import { UserBasicInfo } from '../../entities/user-basic-info';
import { UserBasicInfoComponent } from '../user-basic-info/user-basic-info.component';
import { UserAccessAuthoritiesComponent } from '../user-access-authorities/user-access-authorities.component';
import { UserInfoService } from '../../services/user-info.service';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { Router } from '@angular/router';
import { CommonDialogCallback } from 'src/app/common/interfaces/common-dialog-callback';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { UserQueryConditionsComponent } from '../user-query-conditions/user-query-conditions.component';

@Component({
  selector: 'app-user-add-input',
  templateUrl: './user-add-input.component.html',
  styleUrls: ['./user-add-input.component.css']
})
export class UserAddInputComponent implements OnInit, OnDestroy, CommonDialogCallback {

  private static readonly USER_ADD_ERROR_TITLE = '用户创建失败';
  private static readonly USER_ADD_ERROR_SOURCE_ID = 'SOURCE_ID_ERROR';
  private static readonly USER_ADD_ERROR_TYPE = 'danger';
  // user access authorities
  public currentUserAccessAuthorities: UserAccessAuthorities = null;
  // user header display
  // TODO this will be replace to entity class in future from any type
  public currentUserHeaderDisplays: UserQueryConditionHeader = null;
  // user query condition display
  // TODO this will be replace to entity class in future from any type
  public currentUserQueryConditionDisplays: UserQueryConditionHeader = null;
  // user basic info
  public currentUserBasicInfo: UserBasicInfo = null;

  @ViewChild('userEditContainer', { read: ViewContainerRef }) container: ViewContainerRef;
  componentRefBasicInfo: ComponentRef<UserBasicInfoComponent>;
  componentRefAccessAuthorities: ComponentRef<UserAccessAuthoritiesComponent>;
  componentRefQueryConditionDisplays: ComponentRef<UserQueryConditionsComponent>;

  private openID: string = null;
  constructor(private userInfoService: UserInfoService,
    private resolver: ComponentFactoryResolver,
    private router: Router,
    private commonUtilitiesService: CommonUtilitiesService,
    private currentUserContainer: CurrentUserContainerService) {
  }
  // set userBasicInfo
  public setUserBasicInfo(userBasicInfo: UserBasicInfo) {
    // this.currentUserBasicInfo = userBasicInfo;
    this.currentUserBasicInfo = new UserBasicInfo();
    this.currentUserBasicInfo['userID'] = userBasicInfo['userID'];
    this.currentUserBasicInfo['名字'] = userBasicInfo['名字'];
    this.currentUserBasicInfo['国家'] = userBasicInfo['国家'];
    this.currentUserBasicInfo['地址'] = userBasicInfo['地址'];
    this.currentUserBasicInfo['城市'] = userBasicInfo['城市'];
    this.currentUserBasicInfo['密码'] = userBasicInfo['密码'];
    this.currentUserBasicInfo['年龄'] = userBasicInfo['年龄'];
    this.currentUserBasicInfo['性别'] = userBasicInfo['性别'];
    this.currentUserBasicInfo['昵称'] = userBasicInfo['昵称'];
    this.currentUserBasicInfo['电子邮件'] = userBasicInfo['电子邮件'];
    this.currentUserBasicInfo['电话号码'] = userBasicInfo['电话号码'];
    this.currentUserBasicInfo['省份'] = userBasicInfo['省份'];
  }
  // set user access authorities
  public setUserAccessAuthorites(userAccessAuthorities: UserAccessAuthorities) {
    this.currentUserAccessAuthorities = userAccessAuthorities;
  }
  // set user query conditions displays
  public setUserQueryConditionDisplays(userQueryConditionDisplays: UserQueryConditionHeader) {
    this.currentUserQueryConditionDisplays = userQueryConditionDisplays;
  }
  // set user header displays
  public setUserHeaderDisplays(userHeaderDisplays: UserQueryConditionHeader) {
    this.currentUserHeaderDisplays = userHeaderDisplays;
  }
  // callback to finish getting user detail info
  public callbackToFinsihGetUserDetaildInfo() {
    // show basic info tab
    this.createComponent('basic-info');
  }
  // set openid
  public setOpenID(openID: string) {
    this.openID = openID;
  }
  ngOnInit() {
    // get default user basic info and detailed info
    this.userInfoService.getDefaultBasicInfo(this);
  }

  createComponent(type: string) {
    this.container.clear();
    if (type === 'basic-info') {
      const factory = this.resolver.resolveComponentFactory(UserBasicInfoComponent);
      this.componentRefBasicInfo = this.container.createComponent(factory);
      this.currentUserBasicInfo.userID = this.openID;
      this.componentRefBasicInfo.instance.currentUser = this.currentUserBasicInfo;
    } else if (type === 'access-authorities') {
      const factory = this.resolver.resolveComponentFactory(UserAccessAuthoritiesComponent);
      this.componentRefAccessAuthorities = this.container.createComponent(factory);
      this.currentUserAccessAuthorities.userID = this.openID;
      this.componentRefAccessAuthorities.instance.setCurrentUserAccessAuthorities(
        this.currentUserAccessAuthorities);
    } else if (type === 'query-condition-displays') {
      const factory = this.resolver.resolveComponentFactory(UserQueryConditionsComponent);
      this.componentRefQueryConditionDisplays = this.container.createComponent(factory);
      this.currentUserQueryConditionDisplays.userID = this.openID;
      this.componentRefQueryConditionDisplays.instance.setQueryConditionDisplays(
        this.currentUserQueryConditionDisplays);
      this.componentRefQueryConditionDisplays.instance.setUserAccessAuthorities(this.currentUserAccessAuthorities);
    }
  }
  // add user info to spring
  public addUserInfo(): void {
    // basic info
    let basicInfo: UserBasicInfo = null;
    if (this.componentRefBasicInfo !== undefined) {
      basicInfo = this.componentRefBasicInfo.instance.getCurrentUserBasicInfo();
    } else {
      basicInfo = this.currentUserBasicInfo;
    }
    // access authorities
    let accessAuthorities: UserAccessAuthorities = null;
    if (this.componentRefAccessAuthorities !== undefined) {
      accessAuthorities = this.componentRefAccessAuthorities.instance.
        getCurrentUserAccessAuthorities();
    } else {
      accessAuthorities = this.currentUserAccessAuthorities;
    }
    // query condition displays
    let queryConditionDisplays = null;
    if (this.componentRefQueryConditionDisplays !== undefined) {
      queryConditionDisplays = this.componentRefQueryConditionDisplays
        .instance.getQueryCondtionDisplays();
    } else {
      queryConditionDisplays = this.currentUserQueryConditionDisplays;
    }
    // call service
    this.userInfoService.addUserInfo(
      this,
      basicInfo,
      accessAuthorities,
      this.currentUserHeaderDisplays,
      queryConditionDisplays);
  }
  // callback to add user
  public addUserInfoCallback(httpResponse: HttpResponse): void {
    if (httpResponse.code === 200) {
      // if ok modal dialog is closed, then navigate to user conf component
      // with parameter of user_add_end
      this.router.navigate(['/web/main/user-conf', 'user_add_end']);
    } else if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    } else {
      // get message from response when add user failed
      const failedReason = httpResponse.message;
      // show modal dialog of error
      this.commonUtilitiesService.showCommonDialog(
        UserAddInputComponent.USER_ADD_ERROR_TITLE,
        failedReason,
        UserAddInputComponent.USER_ADD_ERROR_TYPE,
        this,
        UserAddInputComponent.USER_ADD_ERROR_SOURCE_ID);
    }
  }
  // destroy sub component when destroyed
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
    if (sourceID === UserAddInputComponent.USER_ADD_ERROR_SOURCE_ID) {
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
