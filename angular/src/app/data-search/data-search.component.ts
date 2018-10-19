import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { URLSearchParams } from '@angular/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Product } from './Product';
import { Header } from './Header';
import 'jquery';
import 'bootstrap';
import 'bootstrap-datepicker';
import 'bootstrap-table';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Component({
  selector: 'app-data-search',
  templateUrl: './data-search.component.html',
  styleUrls: ['./data-search.component.css']
})
export class DataSearchComponent implements OnInit {
  private productsUrl = 'api/products';  // URL to web api
  private searchUrl = 'api/search';  // URL to web api
  private headersUrl = 'api/headers';  // URL to web api
  private testUrl = 'api/test';  // URL to web api
  public products: Product[] = [];
  public hsCode: string;
  // start date
  startDate: Date;
  // end date
  endDate: Date;
  // search parameters
  searchParam = [
      { key: '海关编码', value: 'test' },
      { key: '起始日期', value: '' },
      { key: '结束日期', value: '' },
      { key: '商品名称', value: '' },
      { key: '贸易方式', value: '' },
      { key: '企业名称', value: '' },
      { key: '收发货地', value: '' }
    ];

  onSearch(): void {
    // this.searchProducts().subscribe(products =>
    //   this.getProductsNotification(products));
    console.log('海关编码为' + this.searchParam[0].value);
  }
  /** GET heroes from the server */
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.productsUrl);
  }
  /**get headers from in memory api */
  getHeaders(): Observable<Header[]> {
    return this.http.get<Header[]>(this.headersUrl);
  }
  /** search heroes from the server */
  searchProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.searchUrl);
  }
  /** search products from the server */
  searchTest(): Observable<any> {
    return this.http.post<any>(this.testUrl, this.searchParam, httpOptions);
  }
  constructor(private http: HttpClient) {
    this.startDate = new Date();
    this.endDate = new Date();
  }

  getProductsNotification(products: Product[]) {
    this.products = products;
    $('#table').bootstrapTable('load', this.products);
  }
  searchTestNotification(result: any) {
    alert('result is ' + result.code);
  }
  getHeadersNotification(headers: Header[]) {
    // if donot destroy table at first, table will not be shown
    $('#table').bootstrapTable('destroy');
    // show table's columns
    $('#table').bootstrapTable({ columns: headers });
    // show all products after headers are shown
    this.getProducts().subscribe(products =>
      this.getProductsNotification(products));
  }

  ngOnInit() {
    // get headers from in memory api
    this.getHeaders().subscribe(headers =>
      this.getHeadersNotification(headers));
    this.searchTest().subscribe(result =>
      this.searchTestNotification(result));
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
  }
}
