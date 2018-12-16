import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { UserAccessAuthoritiesComponent } from '../user-access-authorities/user-access-authorities.component';
import { UserBasicInfoComponent } from '../user-basic-info/user-basic-info.component';
import { UserInfoService } from '../../services/user-info.service';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { UserBasicInfo } from '../../entities/user-basic-info';
import { UserQueryConditionHeader } from '../../entities/user-query-condition-header';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {

  // user access authorities
  public currentUserAccessAuthorities: UserAccessAuthorities = null;
  // user header display
  // TODO this will be replace to entity class in future from any type
  public currentUserHeaderDisplay: UserQueryConditionHeader = null;
  // user query condition display
  // TODO this will be replace to entity class in future from any type
  public currentUserQueryConditionDisplay: UserQueryConditionHeader = null;
  // user basic info
  public currentUser: UserBasicInfo = null;

  @ViewChild('userPermission')
  private userAccessAuthoritiesComponent: UserAccessAuthoritiesComponent;

  @ViewChild('userBasicInfo')
  private userBasicInfoComponent: UserBasicInfoComponent;

  constructor(private activeModal: NgbActiveModal,
    private userInfoService: UserInfoService) {
  }

  ngOnInit() {
    this.userInfoService.getUserDetailedInfo(
      this,
      this.currentUser['userID']);
  }

  close() {
    this.activeModal.close();
  }

  // set user permission after user edit component getting user permission
  public setUserAccessAuthorities(userAccessAuthorities: UserAccessAuthorities) {
    this.currentUserAccessAuthorities = userAccessAuthorities;
    this.userAccessAuthoritiesComponent.setCurrentUserAccessAuthorities(userAccessAuthorities);
  }

  public setUserHeaderDisplay(headerDisplay: any) {
    this.currentUserHeaderDisplay = headerDisplay;
  }
  public setUserQueryConditionDisplay(queryConditionDisplay: any) {
    this.currentUserQueryConditionDisplay = queryConditionDisplay;
  }
  // update user info(basic info, permissions, query contiditons, headers)
  public updateUserInfo() {
    const basicInfo: UserBasicInfo = this.userBasicInfoComponent.
      getCurrentUserBasicInfo();
    const accessAuthorities = this.userAccessAuthoritiesComponent.
      getCurrentUserAccessAuthorities();
    // call service
    this.userInfoService.updateUserInfo(
      this,
      basicInfo,
      accessAuthorities,
      this.currentUserHeaderDisplay,
      this.currentUserQueryConditionDisplay);
  }
  public updateUserInfoCallback(httpResponse: HttpResponse): void {
    if (httpResponse.code === 200) {
      // update ok
      this.activeModal.close();
    } else {
      // TODO update ng
    }
  }
}
