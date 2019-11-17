import { Injectable } from '@angular/core';
import { UserBasicInfo } from 'src/app/user-conf/entities/user-basic-info';
import { UserAccessAuthorities } from 'src/app/user-conf/entities/user-access-authorities';
import { UserQueryConditionHeader } from 'src/app/user-conf/entities/user-query-condition-header';
import { HttpClient } from '@angular/common/http';
import { UserBaiscInfosDictionary } from 'src/app/user-conf/services/user-info.service';
import { UserAccessAuthoritiesDictionary } from 'src/app/user-conf/services/user-info.service';
import { CommonUtilitiesService } from './common-utilities.service';
import { Router } from '@angular/router';
import { ColumnsContainerService } from './columns-container.service';
import { QUERY_CONDITION_HEADER_DICTIONARY } from '../../user-conf/services/user-info.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentUserContainerService {
  public SESSEION_TIME_URL_EXTERNAL = '/web/login/external';
  public SESSEION_TIME_URL_INTERNAL = '/web/login/internal';
  public ADMIN_USER = 'sinoshuju_admin';
  public DEFAULT_USER = 'sinoshuju_default';

  private openID: string = null;

  private userBasicInfo: UserBasicInfo;
  private userAccessAuthorities: UserAccessAuthorities;
  private userQueryConditionDisplays: UserQueryConditionHeader;
  private userHeaderDisplays: UserQueryConditionHeader;

  constructor(private http: HttpClient,
    private commonUtilitiesService: CommonUtilitiesService,
    private router: Router,
    private columnsContainer: ColumnsContainerService) { }
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
  public getOpenID(): string {
    return this.openID;
  }
  // set openid and user basic info and detail info
  // when input url to go to main component without login in the case of session available
  public saveUserInfo(userInfo: any): void {
    // get all columns
    let queryConditionHeaderDictionary = null;
    if (this.columnsContainer.getAllColumns() !== null) {
      queryConditionHeaderDictionary = this.columnsContainer.getAllColumns();
    } else {
      queryConditionHeaderDictionary = QUERY_CONDITION_HEADER_DICTIONARY;
    }
    // set openid
    this.openID = userInfo.userIDFromBasicInfos;
    // user basic info
    const basicInfos = this.commonUtilitiesService.deserializeDataFromHttpResponse(
      UserBaiscInfosDictionary, userInfo.userBasicInfos);
    this.userBasicInfo = basicInfos;
    // user access authorities
    const userAccessAuthorities = this.commonUtilitiesService.deserializeDataFromHttpResponse(
      UserAccessAuthoritiesDictionary, userInfo.userDetailedInfos.userAccessAuthorities);
    this.userAccessAuthorities = userAccessAuthorities;
    // user query conditions
    const queryConditionDisplays = this.commonUtilitiesService.deserializeDataFromHttpResponse(
      queryConditionHeaderDictionary, userInfo.userDetailedInfos.userQueryConditionDisplays);
    this.userQueryConditionDisplays = queryConditionDisplays;
    // header displays
    const headerDisplays = this.commonUtilitiesService.deserializeDataFromHttpResponse(
      queryConditionHeaderDictionary, userInfo.userDetailedInfos.userHeaderDisplays);
    this.userHeaderDisplays = headerDisplays;
  }
  // when session timeout, navigate /web/login/external
  public sessionTimeout(): void {
    // close processing dialog
    this.commonUtilitiesService.closeProcessingDialog();
    // redirect to login url
    if (this.openID === this.ADMIN_USER ||
      this.openID === this.DEFAULT_USER) {
      this.router.navigate([this.SESSEION_TIME_URL_INTERNAL]);
    } else {
      this.router.navigate([this.SESSEION_TIME_URL_EXTERNAL]);
    }
  }
}
