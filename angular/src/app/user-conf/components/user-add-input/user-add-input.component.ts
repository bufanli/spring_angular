import { Component, OnInit, ViewContainerRef, ViewChild, OnDestroy, ComponentFactoryResolver, ComponentRef } from '@angular/core';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { UserQueryConditionHeader } from '../../entities/user-query-condition-header';
import { UserBasicInfo } from '../../entities/user-basic-info';
import { UserBasicInfoComponent } from '../user-basic-info/user-basic-info.component';
import { UserAccessAuthoritiesComponent } from '../user-access-authorities/user-access-authorities.component';
import { UserInfoService } from '../../services/user-info.service';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-add-input',
  templateUrl: './user-add-input.component.html',
  styleUrls: ['./user-add-input.component.css']
})
export class UserAddInputComponent implements OnInit, OnDestroy {

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

  private openID: string = null;
  constructor(private userInfoService: UserInfoService,
    private resolver: ComponentFactoryResolver,
    private router: Router) {
  }
  // set userBasicInfo
  public setUserBasicInfo(userBasicInfo: UserBasicInfo) {
    this.currentUserBasicInfo = userBasicInfo;
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
      // this.componentRef.instance.output.subscribe((msg: string) => console.log(msg));
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
    // call service
    this.userInfoService.addUserInfo(
      this,
      basicInfo,
      accessAuthorities,
      this.currentUserHeaderDisplays,
      this.currentUserQueryConditionDisplays);
  }
  // callback to add user
  public addUserInfoCallback(httpResponse: HttpResponse): void {
    if (httpResponse.code === 200) {
      // update ok
      this.router.navigate(['/web/main/user-conf', 'user_add_end']);
    } else {
      // TODO update ng
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

}
