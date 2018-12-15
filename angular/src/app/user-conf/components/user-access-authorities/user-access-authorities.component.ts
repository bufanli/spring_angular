import { Component, OnInit, Input, AfterViewInit, AfterViewChecked } from '@angular/core';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';

@Component({
  selector: 'app-user-access-authorities',
  templateUrl: './user-access-authorities.component.html',
  styleUrls: ['./user-access-authorities.component.css']
})
export class UserAccessAuthoritiesComponent implements OnInit, AfterViewChecked {

  @Input() currentUserAccessAuthorities: UserAccessAuthorities;
  // product codes which is shown as , seperator
  constructor(private commonUtilitiesService: CommonUtilitiesService) { }

  public setCurrentUserAccessAuthorities(userAccessAuthorities: UserAccessAuthorities) {
    this.currentUserAccessAuthorities = userAccessAuthorities;
  }
  ngOnInit() {
  }
  ngAfterViewChecked() {
    this.initDatePicker();
  }
  // init date picker
  initDatePicker() {
    // set date picker's formatter
    $('.input-daterange input').each(function () {
      $(this).datepicker({
        format: 'yyyy/mm/dd',
        autoclose: true,
        todayBtn: 'linked',
        language: 'zh-CN',
      });
    });
    // set initial date to datepicker
    const dateArray = this.currentUserAccessAuthorities['日期'].split(
      this.commonUtilitiesService.COMMON_SEPERATOR);
    const startDate = dateArray[0];
    const toDate = dateArray[1];
    // set query start date and to date
    $('#start-time').datepicker('update', startDate);
    $('#to-time').datepicker('update', toDate);
  }

  // set initial product codes
  initQueryProducts() {
    const products = this.currentUserAccessAuthorities['商品编码'].split(
      this.commonUtilitiesService.COMMON_SEPERATOR);
    let productCodes: string = null;
    if (products.length > 1) {
      productCodes = products.join(',');
    } else {
      productCodes = this.currentUserAccessAuthorities['商品编码'];
    }
  }
}
