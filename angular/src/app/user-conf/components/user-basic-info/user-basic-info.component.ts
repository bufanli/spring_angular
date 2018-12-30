import { Component, OnInit, Input, AfterViewInit, AfterContentInit } from '@angular/core';
import { UserBasicInfo } from '../../entities/user-basic-info';
import 'jquery';
import 'bootstrap';
import 'bootstrap-datepicker';
import 'bootstrap-table';
import 'bootstrap-select';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { UUID } from 'angular2-uuid';


@Component({
  selector: 'app-user-basic-info',
  templateUrl: './user-basic-info.component.html',
  styleUrls: ['./user-basic-info.component.css']
})
export class UserBasicInfoComponent implements OnInit, AfterViewInit {
  // because user add input component and
  // user edit component share the user basic info component and
  // user access authorities component, but jquery can not distinguish them by same id name
  // so we have to define a uuid as component's unique id.
  public componentID: string = null;
  // editing user
  @Input() currentUser: UserBasicInfo;
  constructor(public commonUtilitiesService: CommonUtilitiesService) {
  }

  ngOnInit() {
    this.componentID = UUID.UUID();
  }

  ngAfterViewInit(): void {
    // if call selectpicker, select control will not be shown for some reason
    // but call selectpicker can resolve this issue in ngAfterViewInit
    $('#gender' + this.componentID).selectpicker('val', this.currentUser['性别']);
    $('#gender' + this.componentID).selectpicker('refresh');
    $('#province' + this.componentID).selectpicker('val', this.currentUser['省份']);
    $('#province' + this.componentID).selectpicker('refresh');
  }

  // get current user basic info
  getCurrentUserBasicInfo(): UserBasicInfo {
    return this.currentUser;
  }
}
