import { Component, OnInit } from '@angular/core';
import { Header } from 'src/app/common/entities/header';
import { Observable } from 'rxjs';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { UUID } from 'angular2-uuid';
import { HttpClient } from '@angular/common/http';
import { NgbModal, NgbModalOptions, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { DataDictionaryUploadComponent } from '../data-dictionary-upload/data-dictionary-upload.component';
import { ResponseContentType, Http, Headers } from '@angular/http';
import { saveAs as importedSaveAs } from 'file-saver';

// json header for download post
// tslint:disable-next-line: deprecation
const head = new Headers({ 'Content-Type': 'application/json' });
const httpDownloadOptions = {
  headers: head,
  // tslint:disable-next-line: deprecation
  responseType: ResponseContentType.Blob,
};
@Component({
  selector: 'app-data-dictionary',
  templateUrl: './data-dictionary.component.html',
  styleUrls: ['./data-dictionary.component.css']
})
export class DataDictionaryComponent implements OnInit {
  private readonly IMPORT_DICTIONARY_PREFIX = 'import_dictionary_';
  private readonly EXPORT_DICTIONARY_PREFIX = 'export_dictionary_';
  private readonly DELETE_DICTIONARY_PREFIX = 'delete_dictionary_';
  // get data dictionaries
  private getDataDictionariesUrl = 'api/getDataDictionaries';  // URL to get data dictionaries
  // export
  private exportDataDictionaryUrl = 'api/exportDataDictionary';  // URL to get data dictionaries
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
  constructor(private currentUserContainer: CurrentUserContainerService,
    private http: HttpClient,
    private modalService: NgbModal,
    // tslint:disable-next-line: deprecation
    private downloadHttp: Http,
  ) { }

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
    return this.http.get<HttpResponse>(this.getDataDictionariesUrl);
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
        this.EXPORT_DICTIONARY_PREFIX + buttonId + ' class=\'btn btn-primary btn-xs \'>\
      <span class=\'glyphicon glyphicon-cog\'></span> 导出字典</button>';
      const importDataDictionaryButton = '<button type=\'button\' class=\'margin-button btn btn-default btn-xs\' id=' +
        this.IMPORT_DICTIONARY_PREFIX + buttonId + ' class=\'btn btn-primary btn-xs \'>\
      <span class=\'glyphicon glyphicon-trash\'></span> 导入字典</button>';
      const deleteDataDictionaryButton = '<button type=\'button\' class=\'margin-button btn btn-default btn-xs\' id=' +
        this.DELETE_DICTIONARY_PREFIX + buttonId + ' class=\'btn btn-primary btn-xs \'>\
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
    // bind event handler for every page
    $('#table').on('page-change.bs.table', this, this.bindEventHandler);
  }
  // bind event handler for delete, import csv and export csv event
  private bindEventHandler(): void {
    // get current page data
    const currentPageData = $('#table').bootstrapTable('getData');
    // bind user edit event, this.modalService is passed as target.data
    for (let i = 0; i < currentPageData.length; i++) {
      const importDictionaryButtonId = '#' + this.IMPORT_DICTIONARY_PREFIX + currentPageData[i][this.UUID_FIELD];
      const exportDictionaryButtonId = '#' + this.EXPORT_DICTIONARY_PREFIX + currentPageData[i][this.UUID_FIELD];
      const deleteDictionaryButtonId = '#' + this.DELETE_DICTIONARY_PREFIX + currentPageData[i][this.UUID_FIELD];
      // import button
      $(importDictionaryButtonId).on('click', currentPageData[i][this.DATA_DICTIONARY_NAME_FIELD], this.importDictionary);
      // export button
      $(exportDictionaryButtonId).on('click', this, this.exportDictionary);
      // delete button
      $(deleteDictionaryButtonId).on('click', this, this.deleteDictionary);
    }
  }
  // import dictionary
  public importDictionary(dictinoaryName: any): void {
    const service: NgbModal = this.modalService;
    const modalRef = service.open(DataDictionaryUploadComponent, this.adjustModalOptions());
    modalRef.componentInstance.setDictionaryName(dictinoaryName);
  }
  // adjust modal options
  // if don't adjust modal options, modal will not be shown correctly
  private adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    return options;
  }
  // export dictionary
  public exportDictionary(dictinoaryName: any): void {
    this.exoprtDictionaryImpl(dictinoaryName);
  }
  // download data to file
  private async exoprtDictionaryImpl(dictinoaryName: any): Promise<void> {
    const formData = {
      dictionaryName: dictinoaryName,
    };
    return this.downloadHttp.post(this.exportDataDictionaryUrl, JSON.stringify(formData), httpDownloadOptions).toPromise().then(
      res => {
        const tempRes: any = res;
        if (tempRes._body.size === 0) {
          this.currentUserContainer.sessionTimeout();
        } else {
          // get file name from responose
          const contentDisposition = tempRes.headers._headers.get('content-disposition');
          const attachmentAndFileName: string = contentDisposition[0];
          const fileNameIndex = attachmentAndFileName.indexOf('filename=');
          if (fileNameIndex >= 0) {
            const fileName = attachmentAndFileName.substring(fileNameIndex + 'filename='.length);
            importedSaveAs(res.blob(), fileName);
          } else {
            importedSaveAs(res.blob());
          }
        }
      });
  }
  // delete dictionaory
  private deleteDictionary(): void {

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
