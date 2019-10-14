import { Component, OnInit } from '@angular/core';
import { Header } from 'src/app/common/entities/header';
import { Observable } from 'rxjs';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { UUID } from 'angular2-uuid';

@Component({
  selector: 'app-data-dictionary',
  templateUrl: './data-dictionary.component.html',
  styleUrls: ['./data-dictionary.component.css']
})
export class DataDictionaryComponent implements OnInit {
  // operation header index
  private readonly OPERATION_HEADER_INDEX = 2;
  private readonly DATA_DICTIONARY_NAME_FIELD = 'dataDictionaryName';
  private readonly OPERATIONS_FIELD = 'operations';
  private readonly UUID_FIELD = 'uuid';
  private dataDictionariesHeaders: Header[] = [
    new Header(this.UUID_FIELD, this.UUID_FIELD, false),
    new Header(this.DATA_DICTIONARY_NAME_FIELD, '数据字典名称', true),
    new Header(this.OPERATIONS_FIELD, '操作', true),
  ];
  // data dictionary names
  private dataDictionaryNames: string[] = null;
  constructor(private currentUserContainer: CurrentUserContainerService) { }

  ngOnInit() {
    // init table headers
    this.initDataDictionariesTable();
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
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    } else {
      this.dataDictionaryNames = httpResponse.data;
      // load data dictionaries into table
      this.loadDataDictionariesIntoTable();
    }
  }
  // add formatter to user list
  private addOperationFormatter(operationHeader: Header) {
    operationHeader.formatter = function (value, row, index) {
      const buttonId = row[this.UUID_FIELD];
      const exportDataDictionaryButton = '<button type=\'button\' class=\'margin-button btn btn-primary btn-xs\' id=' +
        'export_dictionary_' + buttonId + ' class=\'btn btn-primary btn-xs \'>\
      <span class=\'glyphicon glyphicon-cog\'></span> 导出字典</button>';
      const importDataDictionaryButton = '<button type=\'button\' class=\'margin-button btn btn-default btn-xs\' id=' +
        'import_dictionary_' + buttonId + ' class=\'btn btn-primary btn-xs \'>\
      <span class=\'glyphicon glyphicon-trash\'></span> 导入字典</button>';
      const deleteDataDictionaryButton = '<button type=\'button\' class=\'margin-button btn btn-default btn-xs\' id=' +
        'import_dictionary_' + buttonId + ' class=\'btn btn-primary btn-xs \'>\
      <span class=\'glyphicon glyphicon-trash\'></span> 删除字典</button>';

      return exportDataDictionaryButton + importDataDictionaryButton + deleteDataDictionaryButton;
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
  // load data dictionaries into table
  private loadDataDictionariesIntoTable(): void {
    // data dictionary table data
    const dataDictionaryTableData: any[] = [];
    this.dataDictionaryNames.forEach(element => {
      const tableDataEntry: any = {};
      // data dictionary name
      tableDataEntry[this.DATA_DICTIONARY_NAME_FIELD] = element;
      dataDictionaryTableData.push(tableDataEntry);
      // uuid
      const uuid = UUID.UUID();
      tableDataEntry[this.UUID_FIELD] = uuid;
    });
    $('#table').bootstrapTable('load', dataDictionaryTableData);
  }
}
