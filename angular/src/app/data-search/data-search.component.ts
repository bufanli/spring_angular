import { Component, OnInit} from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Product } from './Product';
import { Header } from './Header';
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
  private searchUrl = 'api/search';  // URL to web api
  private headersUrl = 'api/headers';  // URL to web api
  public products: Product[] = [];
  public hsCode: string;
  // start date
  startDate: Date;
  // end date
  endDate: Date;

  onSearch(): void {
    this.searchProducts().subscribe(products =>
      this.getProductsNotification(products));
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

  constructor(private http: HttpClient) {
    this.startDate = new Date();
    this.endDate = new Date();
  }

  getProductsNotification(products: Product[]) {
    this.products = products;
    $('#table').bootstrapTable('load', this.products);
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
