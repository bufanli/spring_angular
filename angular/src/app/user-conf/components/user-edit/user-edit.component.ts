import { Component, OnInit, ViewChild, ViewContainerRef, ComponentRef, OnDestroy } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { UserAccessAuthoritiesComponent } from '../user-access-authorities/user-access-authorities.component';
import { UserBasicInfoComponent } from '../user-basic-info/user-basic-info.component';
import { UserInfoService } from '../../services/user-info.service';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { UserBasicInfo } from '../../entities/user-basic-info';
import { UserQueryConditionHeader } from '../../entities/user-query-condition-header';
import { ComponentFactoryResolver, ComponentFactory } from '@angular/core';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit, OnDestroy {

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

  @ViewChild('userEditContainer', { read: ViewContainerRef }) container: ViewContainerRef;
  componentRefBasicInfo: ComponentRef<UserBasicInfoComponent>;
  componentRefAccessAuthorities: ComponentRef<UserAccessAuthoritiesComponent>;

  constructor(private activeModal: NgbActiveModal,
    private userInfoService: UserInfoService,
    private resolver: ComponentFactoryResolver) {
  }

  createComponent(type: string) {
    this.container.clear();
    if (type === 'basic-info') {
      const factory = this.resolver.resolveComponentFactory(UserBasicInfoComponent);
      this.componentRefBasicInfo = this.container.createComponent(factory);
      this.componentRefBasicInfo.instance.currentUser = this.currentUser;
      // this.componentRef.instance.output.subscribe((msg: string) => console.log(msg));
    } else if (type === 'access-authorities') {
      const factory = this.resolver.resolveComponentFactory(UserAccessAuthoritiesComponent);
      this.componentRefAccessAuthorities = this.container.createComponent(factory);
      this.componentRefAccessAuthorities.instance.setCurrentUserAccessAuthorities(this.currentUserAccessAuthorities);
      // this.componentRef.instance.output.subscribe((msg: string) => console.log(msg));
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
    const basicInfo: UserBasicInfo = this.componentRefBasicInfo.instance.
      getCurrentUserBasicInfo();
    const accessAuthorities = this.componentRefAccessAuthorities.instance.
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
  ngOnDestroy() {
    if (this.componentRefBasicInfo != null) {
      this.componentRefBasicInfo.destroy();
    }
    if (this.componentRefAccessAuthorities != null) {
      this.componentRefAccessAuthorities.destroy();
    }
  }
}
