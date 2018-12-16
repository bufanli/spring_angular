import { Component, OnInit, Input, AfterViewInit, AfterContentInit } from '@angular/core';
import { UserBasicInfo } from '../../entities/user-basic-info';
import 'jquery';
import 'bootstrap';
import 'bootstrap-datepicker';
import 'bootstrap-table';
import 'bootstrap-select';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';


@Component({
  selector: 'app-user-basic-info',
  templateUrl: './user-basic-info.component.html',
  styleUrls: ['./user-basic-info.component.css']
})
export class UserBasicInfoComponent implements OnInit, AfterViewInit {
  // editing user
  @Input() currentUser: UserBasicInfo;
  constructor(public commonUtilitiesService: CommonUtilitiesService) {
  }

  ngOnInit() {
    // if call selectpicker, select control will not be shown for some reason
    // but call selectpicker can resolve this issue in ngAfterViewInit
    // $('#gender').selectpicker('destroy');
    // $('#gender').selectpicker();
  }

  ngAfterViewInit(): void {
    $('#gender').selectpicker('val', this.currentUser['性别']);
    $('#gender').selectpicker('refresh');
    $('#province').selectpicker('val', this.currentUser['省份']);
    $('#province').selectpicker('refresh');
  }

  // get current user basic info
  getCurrentUserBasicInfo(): UserBasicInfo {
    this.currentUser['性别'] = $('#gender').val();
    this.currentUser['省份'] = $('#province').val();
    return this.currentUser;
  }
}
