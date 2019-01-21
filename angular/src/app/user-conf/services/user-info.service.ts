import { Injectable, Query } from '@angular/core';
import { Observable } from 'rxjs';
import { UserEditComponent } from '../components/user-edit/user-edit.component';
import { HttpResponse } from '../../common/entities/http-response';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { UserBasicInfo } from '../entities/user-basic-info';
import { UserAccessAuthorities } from '../entities/user-access-authorities';
import { UserQueryConditionHeader } from '../entities/user-query-condition-header';
import { UserAddInputComponent } from '../components/user-add-input/user-add-input.component';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

export const UserAccessAuthoritiesDictionary = [
  'userID',
  '日期',
  '商品编码',
  '有效期',
  '上传可否',
  '看的条数',
  '复制数据可否',
  '导出数据可否',
  '查看详细可否',
  '查看详细频度',
  '复制详细可否',
  '下载详细可否',
  '查看透视图可否',
  '导出透视图可否',
  '查看数据图可否',
  '导出数据图可否',
];
export const UserBaiscInfosDictionary = [
  'id',
  'userID',
  '昵称',
  '性别',
  '名字',
  '密码',
  '年龄',
  '国家',
  '省份',
  '城市',
  '地址',
  '手机号码',
  '电子邮件',
];
export const QueryConditionHeaderDictionary = [
  'userID',
  '日期',
  '进口',
  '进口关区',
  '主管关区',
  '装货港',
  '中转国',
  '原产国',
  '商品编码',
  '产品名称',
  '厂号',
  '生产工厂',
  '牛种',
  '级别',
  '饲料',
  '规格型号',
  '成交方式',
  '申报单价',
  '申报总价',
  '申报币制',
  '美元单价',
  '美元总价',
  '美元币制',
  '申报数量',
  '申报数量单位',
  '法定重量',
  '法定单位',
  '毛重（整个报关单）',
  '净重（整个报关单）',
  '重量单位',
  '贸易方式',
  '运输方式',
  '目的地',
  '包装种类',
  '申报单位',
  '货主单位',
  '经营单位',
  '企业代码',
  '企业性质',
  '地址',
  '电话',
  '传真',
  '手机',
  '网站',
  '邮编',
  'Email',
  '联系人',
];
// user permission info except basic info
@Injectable({
  providedIn: 'root'
})
export class UserInfoService {

  private getUserDetailedInfosUrl = 'api/getUserDetailedInfos';  // URL to get user's permission
  private getDefaultUserBasicInfoUrl = 'api/getUserDefaultBasicInfo';  // URL to get user's basic info
  private getDefaultUserDetailedInfoUrl = 'api/getUserDefaultDetailedInfos';  // URL to get user's detailed info
  private updateUserInfoUrl = 'api/updateUser';  // URL to update user
  private addUserInfoUrl = 'api/addUser';  // URL to add user
  private userEditComponent: UserEditComponent = null;
  private userAddInputComponent: UserAddInputComponent = null;

  constructor(private http: HttpClient,
    private commonUtilityService: CommonUtilitiesService) { }

  public getUserDetailedInfo(sourceComponent: UserEditComponent, userID: string): void {
    this.userEditComponent = sourceComponent;
    this.getUserDetailedInfoImpl(userID).subscribe(
      httpResponse => this.getUserDetailedInfoNotification(httpResponse));
  }
  public getUserDetailedInfoImpl(userID: string): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(`${this.getUserDetailedInfosUrl}/${userID}`);
  }

  public getUserDetailedInfoNotification(httpResponse: HttpResponse) {
    // get permissions from httpResponse
    this.getPermissionsFromHttpResponse(httpResponse);
  }

  private getPermissionsFromHttpResponse(httpResponse: HttpResponse): void {
    // check session timeout
    this.userEditComponent.checkSessionTimeout(httpResponse);
    const data: any = httpResponse.data;
    if (data == null) {
      return null;
    }

    // get user access authroties
    const userAccessAuthoritiesData = data.userAccessAuthorities;
    const userAccessAuthorities = this.commonUtilityService.deserializeDataFromHttpResponse(
      UserAccessAuthoritiesDictionary, userAccessAuthoritiesData);
    this.userEditComponent.setUserAccessAuthorities(userAccessAuthorities);

    // get header display
    const userHeaderDisplays = data.userHeaderDisplays;
    const headerDisplays = this.commonUtilityService.deserializeDataFromHttpResponse(
      QueryConditionHeaderDictionary,
      userHeaderDisplays);
    this.userEditComponent.setUserHeaderDisplay(headerDisplays);

    // get query condition display
    const userQueryConditionDisplays = data.userQueryConditionDisplays;
    const queryConditionDisplays = this.commonUtilityService.deserializeDataFromHttpResponse(
      QueryConditionHeaderDictionary,
      userQueryConditionDisplays
    );
    this.userEditComponent.setUserQueryConditionDisplay(queryConditionDisplays);
  }

  public updateUserInfo(
    userEditComponent: UserEditComponent,
    basicInfo: UserBasicInfo,
    userAccessAuthorities: UserAccessAuthorities,
    userHeaderDisplay: any,
    userQueryConditionDisplay: any): void {
    this.updateUserInfoImpl(
      basicInfo,
      userAccessAuthorities,
      userHeaderDisplay,
      userQueryConditionDisplay).subscribe(
        httpResponse => this.updateUserInfoNotification(userEditComponent, httpResponse));
  }
  private updateUserInfoImpl(
    userBasicInfo: UserBasicInfo,
    userAccessAuthorities: UserAccessAuthorities,
    userHeaderDisplay: UserQueryConditionHeader,
    userQueryConditionDisplay: UserQueryConditionHeader): Observable<HttpResponse> {
    // combine four parts into one parameter
    // user basic info
    const userBasicInfos = this.commonUtilityService.serializeDataToHttpResponse(
      UserBaiscInfosDictionary,
      userBasicInfo);
    // user detail info
    const accessAuthorities = this.commonUtilityService.serializeDataToHttpResponse(
      UserAccessAuthoritiesDictionary,
      userAccessAuthorities
    );
    // header display
    const headerDisplay = this.commonUtilityService.serializeDataToHttpResponse(
      QueryConditionHeaderDictionary,
      userHeaderDisplay
    );
    // query condition display
    const queryConditionDisplay = this.commonUtilityService.serializeDataToHttpResponse(
      QueryConditionHeaderDictionary,
      userQueryConditionDisplay
    );
    const userDetailInfo = {
      userAccessAuthorities: accessAuthorities,
      userQueryConditionDisplays: queryConditionDisplay,
      userHeaderDisplays: headerDisplay,
    };
    const updateUserInfoParam = {
      userBasicInfos: userBasicInfos,
      userDetailedInfos: userDetailInfo,
    };
    return this.http.post<HttpResponse>(
      this.updateUserInfoUrl,
      updateUserInfoParam,
      httpOptions);
  }
  private updateUserInfoNotification(
    userEditComponent: UserEditComponent,
    httpResponse: HttpResponse) {
    userEditComponent.updateUserInfoCallback(httpResponse);
  }
  // get user default basic info
  public getDefaultBasicInfo(userAddInputComponent: UserAddInputComponent): void {
    this.userAddInputComponent = userAddInputComponent;
    this.getDefaultBasicInfoImpl().subscribe(httpResponse =>
      this.getDefaultBasicInfoNotification(httpResponse));
  }
  // get user default basic info implementation
  private getDefaultBasicInfoImpl(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getDefaultUserBasicInfoUrl);
  }
  // get user default basic info notification
  private getDefaultBasicInfoNotification(httpResponse: HttpResponse): void {
    // check session timeout
    this.userAddInputComponent.checkSessionTimeout(httpResponse);
    // get default basic info
    // reshape user basic info
    const reshapedResult = this.commonUtilityService.reshapeData(httpResponse.data);
    this.userAddInputComponent.setUserBasicInfo(reshapedResult[0]);
    // get user default detailed info continously
    this.getDefaultDetailedInfo();
  }
  // get default user detailed info
  private getDefaultDetailedInfo(): void {
    this.getDefaultDetailedInfoImpl().subscribe(httpResponse =>
      this.getDefaultDetailedInfoNotification(httpResponse));
  }
  // get default user detailed info implementation
  private getDefaultDetailedInfoImpl(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getDefaultUserDetailedInfoUrl);
  }
  // get default user detailed info notification
  private getDefaultDetailedInfoNotification(httpResponse: HttpResponse): void {
    // check session timeout
    this.userAddInputComponent.checkSessionTimeout(httpResponse);
    // get access authorities
    const userAccessAuthorities = this.commonUtilityService.deserializeDataFromHttpResponse(
      UserAccessAuthoritiesDictionary, httpResponse.data.userAccessAuthorities);
    this.userAddInputComponent.setUserAccessAuthorites(userAccessAuthorities);
    // get query condition displays
    const queryConditionDisplays = this.commonUtilityService.deserializeDataFromHttpResponse(
      QueryConditionHeaderDictionary, httpResponse.data.userQueryConditionDisplays);
    this.userAddInputComponent.setUserQueryConditionDisplays(queryConditionDisplays);
    // get header displays
    const headerDisplay = this.commonUtilityService.deserializeDataFromHttpResponse(
      QueryConditionHeaderDictionary, httpResponse.data.userHeaderDisplays);
    this.userAddInputComponent.setUserHeaderDisplays(headerDisplay);
    // call back to finish get user detailed info
    this.userAddInputComponent.callbackToFinsihGetUserDetaildInfo();
  }

  // add user info to spring
  public addUserInfo(userAddInputComponent: UserAddInputComponent,
    userBasicInfo: UserBasicInfo,
    userAccessAuthorities: UserAccessAuthorities,
    userQueryConditionDisplay: UserQueryConditionHeader,
    userHeaderDisplay: UserQueryConditionHeader): void {
    // unify all user id as basic info's user id
    userAccessAuthorities['userID'] = userBasicInfo['userID'];
    userQueryConditionDisplay['userID'] = userBasicInfo['userID'];
    userHeaderDisplay['userID'] = userBasicInfo['userID'];
    this.addUserInfoImpl(
      userBasicInfo,
      userAccessAuthorities,
      userHeaderDisplay,
      userQueryConditionDisplay).subscribe(
        httpResponse => this.addUserInfoNotification(userAddInputComponent, httpResponse));

  }
  // add user info implemetation
  private addUserInfoImpl(
    userBasicInfo: UserBasicInfo,
    userAccessAuthorities: UserAccessAuthorities,
    userHeaderDisplay: UserQueryConditionHeader,
    userQueryConditionDisplay: UserQueryConditionHeader): Observable<HttpResponse> {
    // combine four parts into one parameter
    // user basic info
    const userBasicInfos = this.commonUtilityService.serializeDataToHttpResponse(
      UserBaiscInfosDictionary,
      userBasicInfo);
    // user detail info
    const accessAuthorities = this.commonUtilityService.serializeDataToHttpResponse(
      UserAccessAuthoritiesDictionary,
      userAccessAuthorities
    );
    // header display
    const headerDisplay = this.commonUtilityService.serializeDataToHttpResponse(
      QueryConditionHeaderDictionary,
      userHeaderDisplay
    );
    // query condition display
    const queryConditionDisplay = this.commonUtilityService.serializeDataToHttpResponse(
      QueryConditionHeaderDictionary,
      userQueryConditionDisplay
    );
    const userDetailInfo = {
      userAccessAuthorities: accessAuthorities,
      userQueryConditionDisplays: queryConditionDisplay,
      userHeaderDisplays: headerDisplay,
    };
    const addUserInfoParam = {
      userBasicInfos: userBasicInfos,
      userDetailedInfos: userDetailInfo,
    };
    return this.http.post<HttpResponse>(
      this.addUserInfoUrl,
      addUserInfoParam,
      httpOptions);
  }
  private addUserInfoNotification(
    userAddInputComponent: UserAddInputComponent,
    httpResponse: HttpResponse) {
    userAddInputComponent.addUserInfoCallback(httpResponse);
  }
}
