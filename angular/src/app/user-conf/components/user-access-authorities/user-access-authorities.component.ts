import { Component, OnInit, Input, AfterViewInit, AfterViewChecked } from '@angular/core';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { UUID } from 'angular2-uuid';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { UserInfoService } from '../../services/user-info.service';

@Component({
  selector: 'app-user-access-authorities',
  templateUrl: './user-access-authorities.component.html',
  styleUrls: ['./user-access-authorities.component.css']
})
export class UserAccessAuthoritiesComponent implements OnInit, AfterViewInit, AfterViewChecked {

  // because user add input component and
  // user edit component share the user basic info component and
  // user access authorities component, but jquery can not distinguish them by same id name
  // so we have to define a uuid as component's unique id.
  public componentID: string = null;

  public currentUserAccessAuthorities: UserAccessAuthorities;
  public limitDate: string = null;
  // hs code(海关编码) selections
  public hsCodes: string[] = null;
  public hsCodesChanged = false;
  public selectedHsCodes: string[] = null;

  constructor(private commonUtilitiesService: CommonUtilitiesService,
    private currentUserContainerService: CurrentUserContainerService,
    private userInfoService: UserInfoService) {
    this.componentID = UUID.UUID();
  }

  // set hs code selections
  public setHsCodeSelections(hsCodeSelections: string[]): void {
    this.hsCodes = hsCodeSelections;
    // concatenate hs code selection with selected hs codes
    this.hsCodes = this.hsCodes.concat(this.selectedHsCodes);
    this.hsCodesChanged = true;
  }

  // check session timeout
  public checkSessionTimeout(httpResponse: any): void {
    if (httpResponse.code === 201) {
      this.currentUserContainerService.sessionTimeout();
    }
  }
  public setCurrentUserAccessAuthorities(userAccessAuthorities: UserAccessAuthorities) {
    this.currentUserAccessAuthorities = userAccessAuthorities;
    this.setQueryProducts();
    this.setDatePickerValue();
  }
  ngAfterViewInit() {
    this.setDatePickerValue();
    // initialize hs code selections
    this.commonUtilitiesService.setSelectOptions('#hs_code_' + this.componentID, true);
  }
  // ngOnInit
  ngOnInit() {
    this.userInfoService.getHsCodeSelections(this);
  }
  // if hs code changed, refresh hs code selections
  ngAfterViewChecked(): void {
    if (this.hsCodesChanged === true) {
      $('#hs_code_' + this.componentID).selectpicker('refresh');
      this.hsCodesChanged = false;
    }
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
    this.selectedHsCodes = [];
    const hsCodeTemp = this.currentUserAccessAuthorities['海关编码'].split(
      this.commonUtilitiesService.DATA_COMMON_SEPERATOR);
    hsCodeTemp.forEach(element => {
      if (element !== null && element !== '') {
        this.selectedHsCodes.push(element);
      }
    });
  }
  // get current user access authorities
  getCurrentUserAccessAuthorities(): UserAccessAuthorities {
    if (this.selectedHsCodes === null || this.selectedHsCodes.length === 0) {
      // no product limit
      this.currentUserAccessAuthorities['海关编码'] = this.commonUtilitiesService.DATA_COMMON_SEPERATOR;
    } else {
      // convert product codes
      this.currentUserAccessAuthorities['海关编码'] =
        this.commonUtilitiesService.convertArrayCommaSeperatorToDash(this.selectedHsCodes);
    }
    return this.currentUserAccessAuthorities;
  }
  // tell current userID is system reserved user or not
  public isSystemReservedUser(): boolean {
    return this.commonUtilitiesService.isSystemReservedUser(this.currentUserAccessAuthorities['userID']);
  }
}
