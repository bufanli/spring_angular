import { Component, OnInit } from '@angular/core';
import { Header } from 'src/app/common/entities/header';
import { Observable } from 'rxjs';
import { HttpResponse } from 'src/app/common/entities/http-response';

@Component({
  selector: 'app-data-dictionary',
  templateUrl: './data-dictionary.component.html',
  styleUrls: ['./data-dictionary.component.css']
})
export class DataDictionaryComponent implements OnInit {
  // operation header index
  private readonly OPERATION_HEADER_INDEX = 2;
  private dataDictionariesHeaders: Header[] = [
    new Header('id', 'id', false),
    new Header('dataDictionaryName', '数据字典名称', true),
    new Header('operations', '操作', true),
  ];

  constructor() { }

  ngOnInit() {
    // get columns and synonyms
    this.getDataDictionaries().subscribe(httpResponse =>
      this.getDataDictionariesNotification(httpResponse));
    // set size of synonym container
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#table').height($(window).height() * 0.6);
    });
    // set size of synonym container
    $('#table').height($(window).height() * 0.6);
  }
  // get data dictionaries
  private getDataDictionaries(): Observable<HttpResponse> {
    return null;
  }
  // get data dictionaries notification
  private getDataDictionariesNotification(httpResponse: HttpResponse): void {

  }
  // add formatter to user list
  private addOperationFormatter(operationHeader: Header) {
    operationHeader.formatter = function (value, row, index) {
      const buttonId = row.userID;
      const editButton = '<button type=\'button\' class=\'margin-button btn btn-primary btn-xs\' id=' +
        'edit_user_' + buttonId + ' class=\'btn btn-primary btn-xs \'>\
      <span class=\'glyphicon glyphicon-cog\'></span> 编辑</button>';
      const deleteButton = '<button type=\'button\' class=\'margin-button btn btn-default btn-xs\' id=' +
        'delete_user_' + buttonId + ' class=\'btn btn-primary btn-xs \'>\
      <span class=\'glyphicon glyphicon-trash\'></span> 删除</button>';
      return editButton + deleteButton;
    };
  }
  // init data dictionaries table
  private initDataDictionariesTable(): void {
    // set headers for user list
    $('#table').bootstrapTable('destroy');
    // add operation formatter to header
    this.addOperationFormatter(this.dataDictionariesHeaders[this.OPERATION_HEADER_INDEX]);
    $('#table').bootstrapTable({
      columns: this.dataDictionariesHeaders,
      pagination: true,
      pageSize: 8,
      pageList: [],
    });

  }
}
