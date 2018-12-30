import { Component, OnInit, Input, AfterViewInit, AfterViewChecked } from '@angular/core';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { UUID } from 'angular2-uuid';

@Component({
  selector: 'app-user-access-authorities',
  templateUrl: './user-access-authorities.component.html',
  styleUrls: ['./user-access-authorities.component.css']
})
export class UserAccessAuthoritiesComponent implements AfterViewInit {

  // because user add input component and
  // user edit component share the user basic info component and
  // user access authorities component, but jquery can not distinguish them by same id name
  // so we have to define a uuid as component's unique id.
  public componentID: string = null;

  @Input() currentUserAccessAuthorities: UserAccessAuthorities;
  // product codes which is shown as , seperator
  public productCodes: string = null;
  public limitDate: string = null;
  constructor(private commonUtilitiesService: CommonUtilitiesService) {
    this.componentID = UUID.UUID();
   }

  public setCurrentUserAccessAuthorities(userAccessAuthorities: UserAccessAuthorities) {
    this.currentUserAccessAuthorities = userAccessAuthorities;
    this.setQueryProducts();
    this.setDatePickerValue();
  }
  ngAfterViewInit() {
    this.setDatePickerValue();
  }
  // init date picker
  setDatePickerValue() {
    this.setDatePickerFormat('#start-time' + this.componentID);
    this.setDatePickerFormat('#to-time' + this.componentID);
    this.setDatePickerFormat('#expired-time' + this.componentID);
    // set initial date to datepicker
    const dateArray = this.currentUserAccessAuthorities['日期'].split(
      this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    const startDate = dateArray[0];
    const toDate = dateArray[1];
    // set query start date and to date
    $('#start-time' + this.componentID).datepicker('update', startDate);
    $('#to-time' + this.componentID).datepicker('update', toDate);
    // set expired time
    $('#expired-time' + this.componentID).datepicker('update', this.currentUserAccessAuthorities['有效期']);
  }
  // set date picker format
  setDatePickerFormat(controlID: string): void {
    $(controlID).datepicker({
      format: 'yyyy/mm/dd',
      autoclose: true,
      todayBtn: 'linked',
      language: 'zh-CN',
      enableOnReadonly: false,
    });
    $(controlID).on('changeDate', this, this.changeDateHandler);
  }

  // handler of date change
  changeDateHandler(target: any): void {
    // get component
    const component = target.data;
    // put date to component
    if (target.id === '#start-time' + this.componentID || target.id === '#to-time' + this.componentID) {
      // start time
      const startDate = $('#start-time' + this.componentID).datepicker('getDate');
      const startDateStr = component.commonUtilitiesService.convertDateToLocalString(startDate);
      // to time
      const toDate = $('#to-time' + this.componentID).datepicker('getDate');
      const toDateStr = component.commonUtilitiesService.convertDateToLocalString(toDate);
      // convert limit date
      const dateArray = new Array();
      dateArray.push(startDateStr);
      dateArray.push(toDateStr);
      component.currentUserAccessAuthorities['日期'] = dateArray.join(
        component.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    } else {
      // expired time
      const expiredTime = $('#expired-time' + this.componentID).datepicker('getDate');
      component.currentUserAccessAuthorities['有效期'] = component.commonUtilitiesService.convertDateToLocalString(expiredTime);
    }

  }
  // set initial product codes
  setQueryProducts() {
    const products = this.currentUserAccessAuthorities['商品编码'].split(
      this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    if (products.length > 1) {
      this.productCodes = products.join(this.commonUtilitiesService.VIEW_COMMON_SEPERATOR);
    } else {
      this.productCodes = this.currentUserAccessAuthorities['商品编码'];
    }
    // get rid of first view common seperator
    if (this.productCodes.startsWith(this.commonUtilitiesService.VIEW_COMMON_SEPERATOR)) {
      this.productCodes = this.productCodes.slice(1, this.productCodes.length - 1);
    }
    // get rid of last view common seperator
    if (this.productCodes.endsWith(this.commonUtilitiesService.VIEW_COMMON_SEPERATOR)) {
      this.productCodes = this.productCodes.slice(0, this.productCodes.length - 1);
    }
  }
  // get current user access authorities
  getCurrentUserAccessAuthorities(): UserAccessAuthorities {
    // convert product codes
    const productsArray = this.productCodes.split(this.commonUtilitiesService.VIEW_COMMON_SEPERATOR);
    if (productsArray.length > 1) {
      this.currentUserAccessAuthorities['商品编码'] = productsArray.join(
        this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    } else if (productsArray.length === 1) {
      // only one product is permitted
      this.currentUserAccessAuthorities['商品编码'] =
        productsArray[0] + this.commonUtilitiesService.DATA_COMMON_SEPERATOR;
    } else {
      // no product limit
      this.currentUserAccessAuthorities['商品编码'] = this.commonUtilitiesService.DATA_COMMON_SEPERATOR;
    }
    return this.currentUserAccessAuthorities;
  }
}
