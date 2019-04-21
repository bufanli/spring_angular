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

  public currentUserAccessAuthorities: UserAccessAuthorities;
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
    this.setDatePickerFormat('#product-start-time' + this.componentID);
    this.setDatePickerFormat('#product-to-time' + this.componentID);
    this.setDatePickerFormat('#user-start-time' + this.componentID);
    this.setDatePickerFormat('#user-to-time' + this.componentID);
    // set initial date to datepicker
    let dateArray = this.currentUserAccessAuthorities['日期'].split(
      this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    let startDate = dateArray[0];
    let toDate = dateArray[1];
    // set product start date and to date
    $('#product-start-time' + this.componentID).datepicker('update', startDate);
    $('#product-to-time' + this.componentID).datepicker('update', toDate);
    dateArray = this.currentUserAccessAuthorities['有效期'].split(
      this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    startDate = dateArray[0];
    toDate = dateArray[1];
    // set user expired start time and to time
    $('#user-start-time' + this.componentID).datepicker('update', startDate);
    $('#user-to-time' + this.componentID).datepicker('update', toDate);
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
    $(controlID).on('onChange', this, this.changeDateHandler);
  }

  // handler of date change
  changeDateHandler(target: any): void {
    // get component
    const component = target.data;
    // product start time
    let startDate = $('#product-start-time' + component.componentID).datepicker('getDate');
    let startDateStr = component.commonUtilitiesService.convertDateToLocalString(startDate);
    // product to time
    let toDate = $('#product-to-time' + component.componentID).datepicker('getDate');
    let toDateStr = component.commonUtilitiesService.convertDateToLocalString(toDate);
    // convert limit date
    let dateArray = new Array();
    dateArray.push(startDateStr);
    dateArray.push(toDateStr);
    component.currentUserAccessAuthorities['日期'] = dateArray.join(
      component.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    // user start time
    startDate = $('#user-start-time' + component.componentID).datepicker('getDate');
    startDateStr = component.commonUtilitiesService.convertDateToLocalString(startDate);
    // user to time
    toDate = $('#user-to-time' + component.componentID).datepicker('getDate');
    toDateStr = component.commonUtilitiesService.convertDateToLocalString(toDate);
    // convert limit date
    dateArray = new Array();
    dateArray.push(startDateStr);
    dateArray.push(toDateStr);
    component.currentUserAccessAuthorities['有效期'] = dateArray.join(
      component.commonUtilitiesService.DATA_COMMON_SEPERATOR);
  }

  // set initial product codes
  setQueryProducts() {
    const products = this.currentUserAccessAuthorities['海关编码'].split(
      this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    if (products.length > 1) {
      this.productCodes = products.join(this.commonUtilitiesService.VIEW_COMMON_SEPERATOR);
    } else {
      this.productCodes = this.currentUserAccessAuthorities['海关编码'];
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
    if (this.productCodes === null) {
      // no product limit
      this.currentUserAccessAuthorities['海关编码'] = this.commonUtilitiesService.DATA_COMMON_SEPERATOR;
    } else {
      // convert product codes
      const productsArray = this.productCodes.split(this.commonUtilitiesService.VIEW_COMMON_SEPERATOR);
      if (productsArray.length > 1) {
        this.currentUserAccessAuthorities['海关编码'] = productsArray.join(
          this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
      } else if (productsArray.length === 1) {
        // only one product is permitted
        this.currentUserAccessAuthorities['海关编码'] =
          productsArray[0] + this.commonUtilitiesService.DATA_COMMON_SEPERATOR;
      } else {
        // no product limit
        this.currentUserAccessAuthorities['海关编码'] = this.commonUtilitiesService.DATA_COMMON_SEPERATOR;
      }
    }
    return this.currentUserAccessAuthorities;
  }
}
