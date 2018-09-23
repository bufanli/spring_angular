import { Component, OnInit } from '@angular/core';
import 'jquery';
import 'bootstrap';
import 'bootstrap-datepicker';
import 'bootstrap-table';

@Component({
  selector: 'app-data-search',
  templateUrl: './data-search.component.html',
  styleUrls: ['./data-search.component.css']
})
export class DataSearchComponent implements OnInit {

  // start date
  startDate: Date;
  // end date
  endDate: Date;
  constructor() {
    // this.startDate = new Date();
    this.startDate = new Date();
    this.endDate = new Date();
  }

  ngOnInit() {
    // set date picker's formatter
    $('.input-daterange input').each(function () {
      $(this).datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: 'linked',
        language: 'zh-CN',
      });
    });
    // set initial date to datepicker
    $('.input-daterange input').each(function () {
      $(this).datepicker('update', new Date());
    });
    $('#table').bootstrapTable({ // 对应table标签的id
      language: 'zh-CN',
      url: '', // 获取表格数据的url
      cache: false, // 设置为 false 禁用 AJAX 数据缓存， 默认为true
      striped: true,  // 表格显示条纹，默认为false
      pagination: true, // 在表格底部显示分页组件，默认false
      pageList: [10, 20], // 设置页面可以显示的数据条数
      pageSize: 10, // 页面数据条数
      pageNumber: 1, // 首页页码
      sidePagination: 'server', // 设置为服务器端分页
      showHeader: true,
      showRefresh: true,
      showToggle: false,
      showColumns: false,
      showFullscreen: false,
      search: true,
      queryParams: function (params) { // 请求服务器数据时发送的参数，可以在这里添加额外的查询参数，返回false则终止请求
        return {
          pageSize: params.limit, // 每页要显示的数据条数
          offset: params.offset, // 每页显示数据的开始行号
          sort: params.sort, // 要排序的字段
          sortOrder: params.order, // 排序规则
        };
      },
      sortName: 'date', // 要排序的字段
      sortOrder: 'desc', // 排序规则
      columns: [
        {
          field: 'code', // 返回json数据中的name
          title: '顺序号', // 表格表头显示文字
          align: 'center', // 左右居中
          valign: 'middle' // 上下居中
        },
        {
          field: 'date',
          title: '日期',
          align: 'center',
          valign: 'middle',
          class: 'text-primary',
          sortable: true,
        },
        {
          field: 'hs_code',
          title: 'HS编码',
          align: 'center',
          valign: 'middle',
        },
        {
          field: 'enterprise',
          title: '出口企业',
          align: 'center',
          valign: 'middle',
        },
        {
          field: 'client',
          title: '品牌及客户',
          align: 'center',
          valign: 'middle',
        },
        {
          field: 'description',
          title: '产品描述',
          align: 'center',
          valign: 'middle',
        },
        {
          field: 'country',
          title: '国家',
          align: 'center',
          valign: 'middle',
        },
        {
          field: 'unit_price',
          title: '申报单价',
          sortable: true,
        },
        {
          field: 'total_price',
          title: '申报总价',
          sortable: true,
        },
        {
          field: 'amount',
          title: '数量',
          sortable: true,
        },
        {
          field: 'amount_unit',
          title: '数量单位'
        },
      ],
      onLoadSuccess: function () {  // 加载成功时执行
        // tslint:disable-next-line:no-console
        console.info('加载成功');
      },
      onLoadError: function () {  // 加载失败时执行
        // tslint:disable-next-line:no-console
        console.info('加载数据失败');
      }
    });
  }
}
