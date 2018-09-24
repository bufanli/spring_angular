import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Product } from './Product';
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

  private productsUrl = 'api/products';  // URL to web api
  public products: Product[] = [];
  // start date
  startDate: Date;
  // end date
  endDate: Date;

  /** GET heroes from the server */
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.productsUrl);
  }

  constructor(private http: HttpClient) {
    this.startDate = new Date();
    this.endDate = new Date();
  }

  getProductsNotification(products: Product[]) {
    this.products = products;
    $('#table').bootstrapTable('load', this.products);
  }
  ngOnInit() {
    this.getProducts().subscribe(products =>
      this.getProductsNotification(products));
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
    $('#table').bootstrapTable({
    cache: false,
      striped: true,
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
