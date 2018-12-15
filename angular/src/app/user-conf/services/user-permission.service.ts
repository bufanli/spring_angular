import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserEditComponent } from '../components/user-edit/user-edit.component';
import { HttpResponse } from '../../common/entities/http-response';
import { HttpClient } from '@angular/common/http';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';

const userAccessAuthoritiesDictionary = [
  'userID',
  '日期',
  '商品编码',
  '账号有效期',
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
// user permission info except basic info
@Injectable({
  providedIn: 'root'
})
export class UserPermissionService {

  private getUserDetailedInfosUrl = 'getUserDetailedInfos';  // URL to get user's permission
  private userDetailedInfoComponent: UserEditComponent = null;

  constructor(private http: HttpClient,
    private commonUtilityService: CommonUtilitiesService) { }

  public getUserDetailedInfo(sourceComponent: UserEditComponent, userID: string): void {
    this.userDetailedInfoComponent = sourceComponent;
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
    const data: any = httpResponse.data;
    if (data == null) {
      return null;
    }

    // get user access authroties
    const userAccessAuthoritiesData = data.userAccessAuthorities;
    const userAccessAuthorities = this.commonUtilityService.deserializeDataFromHttpResponse(
      userAccessAuthoritiesDictionary, userAccessAuthoritiesData);
      this.userDetailedInfoComponent.setUserAccessAuthorities(userAccessAuthorities);
  }
}
