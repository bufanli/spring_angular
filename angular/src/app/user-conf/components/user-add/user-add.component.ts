import { Component, OnInit, ViewChild, ViewContainerRef, ComponentRef, ComponentFactoryResolver } from '@angular/core';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { UserQueryConditionHeader } from '../../entities/user-query-condition-header';
import { UserBasicInfo } from '../../entities/user-basic-info';
import { UserAddInputComponent } from '../user-add-input/user-add-input.component';
import { UserAddBarcodeComponent } from '../user-add-barcode/user-add-barcode.component';
import { UserInfoService } from '../../services/user-info.service';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.css']
})
export class UserAddComponent implements OnInit {

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
  // show whether it has passed wechat authentication
  public isPassedWechatAuthentication = false;
  // open id for adding user
  private openID: string = null;
  // failed reason for adding user
  private reason: string = null;

  @ViewChild('userEditContainer', { read: ViewContainerRef }) container: ViewContainerRef;

  componentRefUserAddInput: ComponentRef<UserAddInputComponent>;
  componentRefUserAddBarcode: ComponentRef<UserAddBarcodeComponent>;

  constructor(private userInfoService: UserInfoService,
    private resolver: ComponentFactoryResolver) {
  }
  // create user add input component or user add barcode component
  createComponent(type: string) {
    this.container.clear();
    if (type === 'bar-code') {
      const factory = this.resolver.resolveComponentFactory(UserAddBarcodeComponent);
      this.componentRefUserAddBarcode = this.container.createComponent(factory);
    } else if (type === 'input') {
      const factory = this.resolver.resolveComponentFactory(UserAddInputComponent);
      this.componentRefUserAddInput = this.container.createComponent(factory);
    }
  }

  ngOnInit() {
    // show bar code component at first
    if (this.isPassedWechatAuthentication === false) {
      this.createComponent('bar-code');
      this.componentRefUserAddBarcode.instance.setReason(this.reason);
    } else {
      this.createComponent('input');
      this.componentRefUserAddInput.instance.setOpenID(this.openID);
    }
  }

  // wechat result ok
  public wechatOK(openID: string): void {
    // this.createComponent('input');
    this.isPassedWechatAuthentication = true;
    this.openID = openID;
  }
  // wechat result ng
  public wechatNG(reason: string): void {
    // this.createComponent('bar-code');
    this.isPassedWechatAuthentication = false;
    this.reason = reason;
  }

}
