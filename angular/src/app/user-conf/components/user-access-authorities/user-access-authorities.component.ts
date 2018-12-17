import { Component, OnInit, Input, AfterViewInit, AfterViewChecked } from '@angular/core';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';

@Component({
  selector: 'app-user-access-authorities',
  templateUrl: './user-access-authorities.component.html',
  styleUrls: ['./user-access-authorities.component.css']
})
export class UserAccessAuthoritiesComponent implements AfterViewInit {

  @Input() currentUserAccessAuthorities: UserAccessAuthorities;
  // product codes which is shown as , seperator
  public productCodes: string = null;
  public limitDate: string = null;
  constructor(private commonUtilitiesService: CommonUtilitiesService) { }

  public setCurrentUserAccessAuthorities(userAccessAuthorities: UserAccessAuthorities) {
    this.currentUserAccessAuthorities = userAccessAuthorities;
    this.setQueryProducts();
  }
  ngAfterViewInit() {
    this.setDatePickerFormat('#start-time');
    this.setDatePickerFormat('#to-time');
    this.setDatePickerFormat('#expired-time');
    this.setDatePickerValue();
  }
  // init date picker
  setDatePickerValue() {
    // set initial date to datepicker
    const dateArray = this.currentUserAccessAuthorities['日期'].split(
      this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    const startDate = dateArray[0];
    const toDate = dateArray[1];
    // set query start date and to date
    $('#start-time').datepicker('update', startDate);
    $('#to-time').datepicker('update', toDate);
    // set expired time
    $('#expired-time').datepicker('update', this.currentUserAccessAuthorities['有效期']);
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
    // start time
    const startDate = $('#start-time').datepicker('getDate');
    const startDateStr = this.commonUtilitiesService.convertDateToLocalString(startDate);
    // to time
    const toDate = $('#to-time').datepicker('getDate');
    const toDateStr = this.commonUtilitiesService.convertDateToLocalString(toDate);
    // convert limit date
    const dateArray = new Array();
    dateArray.push(startDateStr);
    dateArray.push(toDateStr);
    this.currentUserAccessAuthorities['日期'] = dateArray.join(
      this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
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
