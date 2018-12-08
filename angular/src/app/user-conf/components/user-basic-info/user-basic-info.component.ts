import { Component, OnInit, Input, AfterViewInit, AfterContentInit } from '@angular/core';
import { User } from '../../entities/user';
import 'jquery';
import 'bootstrap';
import 'bootstrap-datepicker';
import 'bootstrap-table';
import 'bootstrap-select';


@Component({
  selector: 'app-user-basic-info',
  templateUrl: './user-basic-info.component.html',
  styleUrls: ['./user-basic-info.component.css']
})
export class UserBasicInfoComponent implements OnInit, AfterViewInit {
  public CHINA_PROVINCES: string[] = [
    '北京',
    '天津',
    '河北',
    '山西',
    '内蒙古',
    '辽宁',
    '吉林',
    '黑龙江',
    '上海',
    '江苏',
    '浙江',
    '安徽',
    '福建',
    '江西',
    '山东',
    '河南',
    '湖北',
    '湖南',
    '广东',
    '广西',
    '海南',
    '重庆',
    '四川',
    '贵州',
    '云南',
    '西藏',
    '陕西',
    '甘肃省',
    '青海',
    '宁夏',
    '新疆',
    '台湾',
    '香港特别行政区',
    '澳门',
  ];
  // editing user
  @Input() currentUser: User;
  constructor() {
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
}
