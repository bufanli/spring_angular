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
import { UserAccessAuthoritiesComponent } from '../components/user-access-authorities/user-access-authorities.component';
import { CategorySelections } from '../entities/category-selections';
import { ColumnsContainerService } from 'src/app/common/services/columns-container.service';
import { UserListComponent } from '../components/user-list/user-list.component';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

export const UserAccessAuthoritiesDictionary = [
  'userID',
  '日期',
  '海关编码',
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
  '显示查询条件最大数',
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
export const QUERY_CONDITION_HEADER_DICTIONARY = [
  'userID',
  '日期',
  '进出口',
  '申报单位名称',
  '货主单位名称',
  '经营单位名称',
  '经营单位代码',
  '运输工具名称',
  '提运单号',
  '海关编码',
  '附加码',
  '商品名称',
  '部位',
  '包装规格',
  '英文品名',
  '品牌',
  '加工厂号',
  '加工企业名称',
  '牛种',
  '牛龄',
  '级别',
  '饲养方式',
  '申报要素',
  '成交方式',
  '申报单价',
  '申报总价',
  '申报币制',
  '美元单价',
  '美元总价',
  '美元币制',
  '统计人民币价',
  '申报数量',
  '申报数量单位',
  '法定重量',
  '法定单位',
  '毛重',
  '净重',
  '重量单位',
  '件数',
  '监管方式',
  '运输方式',
  '目的地',
  '包装种类',
  '主管关区',
  '报关口岸',
  '装货港',
  '中转国',
  '贸易国',
  '企业性质',
  '运费／率',
  '运费币制',
  '保险费／率',
  '保险费币制',
  '杂费／率',
  '杂费币制',
  '地址',
  '手机',
  '电话',
  '传真',
  '邮编',
  'Email',
  '法人',
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
  private deleteUserInfoUrl = 'api/deleteUser';  // URL to delete user
  private addUserInfoUrl = 'api/addUser';  // URL to add user
  private getCategoryListUrl = 'api/getCategoryList';  // URL to get category list
  private userEditComponent: UserEditComponent = null;
  private userAddInputComponent: UserAddInputComponent = null;
  private userAccessAuthoritiesComponent: UserAccessAuthoritiesComponent = null;
  private userListComponent: UserListComponent = null;
  // hs code catetory name
  private readonly HS_CODE_CATETORY_NAME = '海关编码';
  // query condition header dictionary
  private queryConditionHeaderDictionary: string[] = null;

  constructor(private http: HttpClient,
    private commonUtilityService: CommonUtilitiesService,
    private columnsContainer: ColumnsContainerService) {
    // get all columns
    this.columnsContainer.init();
    if (this.columnsContainer.getAllColumns() !== null) {
      this.queryConditionHeaderDictionary = this.columnsContainer.getAllColumns();
    } else {
      this.queryConditionHeaderDictionary = QUERY_CONDITION_HEADER_DICTIONARY;
    }
  }

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
      this.queryConditionHeaderDictionary,
      userHeaderDisplays);
    this.userEditComponent.setUserHeaderDisplay(headerDisplays);

    // get query condition display
    const userQueryConditionDisplays = data.userQueryConditionDisplays;
    const queryConditionDisplays = this.commonUtilityService.deserializeDataFromHttpResponse(
      this.queryConditionHeaderDictionary,
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
      this.queryConditionHeaderDictionary,
      userHeaderDisplay
    );
    // query condition display
    const queryConditionDisplay = this.commonUtilityService.serializeDataToHttpResponse(
      this.queryConditionHeaderDictionary,
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
      this.queryConditionHeaderDictionary, httpResponse.data.userQueryConditionDisplays);
    this.userAddInputComponent.setUserQueryConditionDisplays(queryConditionDisplays);
    // get header displays
    const headerDisplay = this.commonUtilityService.deserializeDataFromHttpResponse(
      this.queryConditionHeaderDictionary, httpResponse.data.userHeaderDisplays);
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
      this.queryConditionHeaderDictionary,
      userHeaderDisplay
    );
    // query condition display
    const queryConditionDisplay = this.commonUtilityService.serializeDataToHttpResponse(
      this.queryConditionHeaderDictionary,
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

  // get hs code selections
  public getHsCodeSelections(userAccessAuthoritiesComponent: UserAccessAuthoritiesComponent): void {
    this.userAccessAuthoritiesComponent = userAccessAuthoritiesComponent;
    this.getHsCodeSelectionsImpl().subscribe(httpResponse =>
      this.getHsCodeSelectionsNotification(httpResponse));
  }
  // get hs code selections implementation
  private getHsCodeSelectionsImpl(): Observable<HttpResponse> {
    // categories
    const categories: string[] = [this.HS_CODE_CATETORY_NAME];
    // revoke api to get catetory selections
    return this.http.post<HttpResponse>(this.getCategoryListUrl, categories);
  }
  // get category list notification
  private getHsCodeSelectionsNotification(httpResponse: HttpResponse): void {
    if (httpResponse.data === null) {
      return;
    } else {
      const categorySelectionsEntries: CategorySelections[] =
        this.convertHttpResponseToCategorySelections(httpResponse.data);
      categorySelectionsEntries.forEach(element => {
        if (element.getCategory() === this.HS_CODE_CATETORY_NAME) {
          this.userAccessAuthoritiesComponent.setHsCodeSelections(
            element.getSelections());
          return;
        }
      });
      // not found
      return;
    }
  }
  // convert http response to category selections
  private convertHttpResponseToCategorySelections(data: any): CategorySelections[] {
    const categorySelections: CategorySelections[] = [];
    data.forEach(element => {
      // create category selections entry
      const categorySelectionsEntry: CategorySelections
        = new CategorySelections(element.category);
      element.selections.forEach(elementInner => {
        categorySelectionsEntry.pushSelection(elementInner);
      });
      // push it into array
      categorySelections.push(categorySelectionsEntry);
    });
    return categorySelections;
  }
  // delete user
  public deleteUser(userListComponent: UserListComponent, userID: string): void {
    this.userListComponent = userListComponent;
    this.deleteUserImpl(userID).subscribe(httpResponse =>
      this.deleteUserNotification(httpResponse));
  }
  // get hs code selections implementation
  private deleteUserImpl(userID: string): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(`${this.deleteUserInfoUrl}/${userID}`);
  }
  // get category list notification
  private deleteUserNotification(httpResponse: HttpResponse): void {
    if (httpResponse.data === null) {
      return;
    } else {
      // check session timeout
      this.userListComponent.checkSessionTimeout(httpResponse);
      // on delete user
      this.userListComponent.onDeleteUser(httpResponse);
    }
  }
}
