import { Injectable } from '@angular/core';
import { UserBasicInfo } from 'src/app/user-conf/entities/user-basic-info';
import { UserAccessAuthorities } from 'src/app/user-conf/entities/user-access-authorities';
import { UserQueryConditionHeader } from 'src/app/user-conf/entities/user-query-condition-header';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { HttpResponse } from '../entities/http-response';
import { LoginComponent } from 'src/app/login/components/login/login.component';
import { AdminLoginComponent } from 'src/app/login/components/admin-login/admin-login.component';
import { UserBaiscInfosDictionary } from 'src/app/user-conf/services/user-info.service';
import { UserAccessAuthoritiesDictionary } from 'src/app/user-conf/services/user-info.service';
import { QueryConditionHeaderDictionary } from 'src/app/user-conf/services/user-info.service';
import { CommonUtilitiesService } from './common-utilities.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentUserContainerService {

  private GET_USER_BASIC_INFO_URL = '/getUserBasicInfo/';
  private GET_USER_DETAIL_INFO_URL = '/getUserDetailedInfos/';
  private GET_DEFAULT_USER_BASIC_INFO = '/getUserDefaultBasicInfo';

  private openID: string;

  private userBasicInfo: UserBasicInfo;
  private userAccessAuthorities: UserAccessAuthorities;
  private userQueryConditionDisplays: UserQueryConditionHeader;
  private userHeaderDisplays: UserQueryConditionHeader;

  private loginComponent: LoginComponent;
  private adminLoginComponent: AdminLoginComponent;
  private isAdminLogin: boolean;
  constructor(private http: HttpClient,
    private commonUtilitiesService: CommonUtilitiesService) { }
  public getCurrentUserBasicInfo(): UserBasicInfo {
    return this.userBasicInfo;
  }
  public getCurrentUserAccessAuthorities(): UserAccessAuthorities {
    return this.userAccessAuthorities;
  }
  public getCurrentQueryConditionDisplays(): UserQueryConditionHeader {
    return this.userQueryConditionDisplays;
  }
  public getCurrentHeaderDisplayers(): UserQueryConditionHeader {
    return this.userHeaderDisplays;
  }
  // save openid
  public saveUserOpenID(loginComponent: LoginComponent, openID: string): boolean {
    // save component
    this.loginComponent = loginComponent;
    this.isAdminLogin = false;
    // save open id
    this.openID = openID;
    // get user basic info
    this.getUserBasicInfo();
    return true;
  }
  public saveAdminOpenID(adminLoginComponent: AdminLoginComponent, openID: string): void {
    // save component
    this.adminLoginComponent = adminLoginComponent;
    this.isAdminLogin = true;
    // save open id
    this.openID = openID;
    // get user basic info
    this.getUserBasicInfo();
  }
  // get user basic info
  private getUserBasicInfo() {
    this.getUserBasicInfoImpl().subscribe(httpResponse =>
      this.getUserBasicInfoNotification(httpResponse));
  }
  // get user basic implementation
  private getUserBasicInfoImpl(): Observable<HttpResponse> {
    const url = this.GET_USER_BASIC_INFO_URL + this.openID;
    return this.http.get<HttpResponse>(url);
  }
  // get user basic info notification
  private getUserBasicInfoNotification(httpResponse: HttpResponse): void {
    const currentUser: any = this.commonUtilitiesService.deserializeDataFromHttpResponse(
      UserBaiscInfosDictionary,
      httpResponse.data);
    // save user basic info
    this.userBasicInfo = currentUser;
    // get user detail info continously
    this.getUserDetailInfo();
  }
  // get user detail info
  private getUserDetailInfo(): void {
    this.getUserDetailInfoImpl().subscribe(httpResponse =>
      this.getUserDetailInfoNotification(httpResponse));
  }
  // get user detail info implementation
  private getUserDetailInfoImpl(): Observable<HttpResponse> {
    const url = this.GET_USER_DETAIL_INFO_URL + this.openID;
    return this.http.get<HttpResponse>(url);
  }
  // get user detail info notification
  private getUserDetailInfoNotification(httpResponse: HttpResponse) {
    // user access authorities
    const userAccessAuthorities = this.commonUtilitiesService.deserializeDataFromHttpResponse(
      UserAccessAuthoritiesDictionary, httpResponse.data.userAccessAuthorities);
    this.userAccessAuthorities = userAccessAuthorities;
    // user query conditions
    const queryConditionDisplays = this.commonUtilitiesService.deserializeDataFromHttpResponse(
      QueryConditionHeaderDictionary, httpResponse.data.userQueryConditionDisplays);
    this.userQueryConditionDisplays = queryConditionDisplays;
    // header displays
    const headerDisplays = this.commonUtilitiesService.deserializeDataFromHttpResponse(
      QueryConditionHeaderDictionary, httpResponse.data.userHeaderDisplays);
    this.userHeaderDisplays = headerDisplays;
    // call back to component
    if (this.isAdminLogin) {
      // call back with no error(0)
      this.adminLoginComponent.getUserInfoCallback(0);
    } else {
      this.loginComponent.getUserInfoCallback(0);
    }
  }
}
