import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { Observable } from 'rxjs';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { ColumnsDictionary } from '../../entities/columns-dictionary';
import { NgbModal, NgbModalOptions, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { EditSynonymComponent } from '../edit-synonym/edit-synonym.component';
import { SaveColumnDictionaryCallback } from '../../interfaces/save-column-dictionary-callback';
import { EditSynonymBase } from '../../interfaces/edit-synonym-base';
import { SaveColumnSynonymsService } from '../../services/save-column-synonyms.service';
import 'jquery';
import 'bootstrap';
import 'bootstrap-table';
import 'bootstrap-select';
import { AddCustomColumnComponent } from '../add-custom-column/add-custom-column.component';
import { ColumnsContainerService } from 'src/app/common/services/columns-container.service';

@Component({
  selector: 'app-edit-dictionary',
  templateUrl: './edit-dictionary.component.html',
  styleUrls: ['./edit-dictionary.component.css']
})
export class EditDictionaryComponent extends EditSynonymBase implements OnInit, AfterViewChecked {
  public readonly fieldName = 'synonym';
  private readonly getColumnDictionaryURL = 'api/getColumnsDictionary';
  private readonly DELETE_COLUMN_TITLE = '删除';
  private readonly DELETE_COLUMN_NAME = 'deletecolumn_';
  private readonly ADD_COLUMN_NAME = 'add_column';
  private readonly ADD_COLUMN_TITLE = '添加';
  private readonly ADD_COLUMN_HEADER = '添加自定义列';
  private readonly DELETE_SYNONYM_OK = '删除同义词成功';
  private readonly DELETE_COLUMN_OK = '删除自定义列成功';
  public columns: string[] = null;
  private columnsDictionariesLoaded = false;
  private isDeletingSynonym = false;
  private isDeletingColumn = false;
  // id is a dummy attribute, just for compilation
  private id: any;
  // upload error msg
  private uploadErrorMsg: string = null;
  // deleting column
  protected deletingColumn: string = null;
  constructor(private http: HttpClient,
    protected currentUserContainer: CurrentUserContainerService,
    protected saveColumnSynonymsService: SaveColumnSynonymsService,
    public modalService: NgbModal,
    private columnsContainer: ColumnsContainerService) {
    super(currentUserContainer, saveColumnSynonymsService);
  }

  ngOnInit() {
    // get columns and synonyms
    this.getColumnsDictionaryImpl().subscribe(httpResponse =>
      this.getColumnsDictionaryNotification(httpResponse));
    // set size of synonym container
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#synonym-container').height($(window).height() * 0.6);
    });
    // set size of synonym container
    $('#synonym-container').height($(window).height() * 0.6);
  }
  // get columns from server implementation
  private getColumnsDictionaryImpl(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getColumnDictionaryURL);
  }
  private getColumnsDictionaryNotification(httpResponse: HttpResponse): void {
    if (httpResponse.code === 200) {
      // get columns dictionary ok
      const columnsDictionaries: any = httpResponse.data;
      this.convertHttpDataToColumnsDictionaries(columnsDictionaries);
      // convert column dictionary entries to columns
      this.convertColumnDictionariesToColumns();
      // add delete link to columns without synonyms
      this.addDeleteLinkToColumnsWithoutSynonyms();
      // add column definition link
      this.addColumnDefinitionLink();
      // set loaded flag
      this.columnsDictionariesLoaded = true;
    } else if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    }
  }
  // convert column dictionary entries to columns array
  private convertColumnDictionariesToColumns(): void {
    this.columns = [];
    this.columnsDictionaries.forEach(element => {
      this.columns.push(element.getColumnName());
    });
  }
  // convert http data to columns
  private convertHttpDataToColumnsDictionaries(httpData: any): void {
    this.columnsDictionaries = [];
    httpData.forEach(element => {
      const columnsDictionary: ColumnsDictionary = new ColumnsDictionary(
        element.columnName, element.synonyms
      );
      this.columnsDictionaries.push(columnsDictionary);
    });
  }
  // add delete link to columns without synonyms
  private addDeleteLinkToColumnsWithoutSynonyms(): void {
    this.columnsDictionaries.forEach(element => {
      if (element.getSynonyms().length === 0) {
        element.getSynonyms().push(this.DELETE_COLUMN_NAME + element.getUUID());
      }
    });
  }
  // add column definition link
  private addColumnDefinitionLink(): void {
    // add column definition link's synonyms
    const synonyms = [this.ADD_COLUMN_NAME];
    const columnDictionary = new ColumnsDictionary(
      this.ADD_COLUMN_HEADER,
      synonyms
    );
    this.columnsDictionaries.push(columnDictionary);
  }
  // abstract synonyms tables  row by given uuid
  private abstractSynonymRowsByUUID(uuid: string): any[] {
    const synonymsRows = [];
    this.columnsDictionaries.forEach(element => {
      if (uuid === element.getUUID()) {
        const synonyms = element.getSynonyms();
        synonyms.forEach(synonym => {
          const row = { synonym: synonym };
          synonymsRows.push(row);
        });
      }
    });
    return synonymsRows;
  }
  ngAfterViewChecked() {
    // load synonym tables
    const that = this;
    if (this.columnsDictionariesLoaded === true) {
      // refresh synonym tables
      this.refreshAllSynonymTables();
      // setup column selection
      this.setSelectOptions('#column');
      // set back loaded flag to false
      this.columnsDictionariesLoaded = false;
    } else {
      // nothing to do
    }
  }
  // show all synonym tables
  private refreshAllSynonymTables(): void {
    const that = this;
    this.columnsDictionaries.forEach(element => {
      // table header
      const tableHeader = {
        field: 'synonym',
        title: element.getColumnName(),
        formatter: function (value, row, index) { return that.operateFormatter(value, row, index); },
      };
      // show header
      $('#' + element.getUUID()).bootstrapTable({
        columns: [tableHeader],
      });
      // load data
      $('#' + element.getUUID()).bootstrapTable('load', that.abstractSynonymRowsByUUID(element.getUUID()));
      // bind event handler to modify and delete
      this.bindClickEventToSynonym(element);
    });
  }
  // bind click event to synonym row
  private bindClickEventToSynonym(columnDictionary: ColumnsDictionary): void {
    const that = this;
    let index = 0;
    const synonyms = columnDictionary.getSynonyms();
    if (columnDictionary.getColumnName() === this.ADD_COLUMN_HEADER) {
      // if this is add custom column link, bind it to add custom column link
      const addCustomColumnId = 'addcolumn' + columnDictionary.getUUID();
      $('#' + addCustomColumnId).on('click', that, function () { return that.addCustomColumnHandler(); });
    } else {
      if (synonyms.length === 1 && synonyms[0].startsWith(this.DELETE_COLUMN_NAME)) {
        // if this is delete column link
        const deleteColumnId = this.DELETE_COLUMN_NAME + columnDictionary.getUUID();
        $('#' + deleteColumnId).on('click', that, function () {
          return that.deleteColumnHandler(columnDictionary.getUUID());
        });
      } else {
        // else, column has synonyms
        synonyms.forEach(synonym => {
          // generate id as %method%uuid%index
          const modifySynonymId = 'modify_' + columnDictionary.getUUID() + '_' + index;
          const deleteSynonymId = 'delete_' + columnDictionary.getUUID() + '_' + index;
          // bind modify synonym handler
          $('#' + modifySynonymId).on('click', that, function () { return that.modifySynonymHandler(modifySynonymId); });
          // bind delete synonym handler
          $('#' + deleteSynonymId).on('click', that, function () { return that.deleteSynonymHandler(deleteSynonymId); });
          // index increase
          index++;
        });
      }
    }
  }
  // modify synonym handler
  private modifySynonymHandler(linkId: string): void {
    // separate id to three part for getting uuid and index
    // inde==0 method, modify/delete
    // index==1 uuid
    // index==2 index
    const idParts = linkId.split('_');
    const modalService: NgbModal = this.modalService;
    const modalRef = modalService.open(EditSynonymComponent, this.adjustModalOptions());
    modalRef.componentInstance.setColumnsDictionaries(this.columnsDictionaries);
    modalRef.componentInstance.setUUID(idParts[1]);
    modalRef.componentInstance.setEditSynonymIndex(Number(idParts[2]));
    modalRef.componentInstance.refreshDataModel();
    modalRef.componentInstance.notifyClose.subscribe(response => this.callbackOfEditSynonymClosed(response));
  }
  // add custom column handler
  private addCustomColumnHandler(): void {
    // clear info msg and error msg
    this.clearErrorMsg();
    this.clearInfoMsg();
    const modalService: NgbModal = this.modalService;
    const modalRef = modalService.open(AddCustomColumnComponent, this.adjustModalOptions());
    modalRef.componentInstance.setColumnsDictionaries(this.columnsDictionaries);
    modalRef.componentInstance.notifyClose.subscribe(response => this.callbackOfAddColumnClosed(response));
  }
  // delete custom column
  private deleteColumnHandler(uuid: string): void {
    // clear all message
    this.clearInfoMsg();
    this.clearErrorMsg();
    // update column dictionaries
    let index = -1;
    for (let i = 0; i < this.columnsDictionaries.length; i++) {
      if (this.columnsDictionaries[i].getUUID() === uuid) {
        index = i;
        break;
      }
    }
    if (index !== -1) {
      // before saving columns dictionaries, save it
      this.saveOriginalColumnsDictionaries(
        this.DELETE_COLUMN,
        this.columnsDictionaries[index],
        index
      );
      // set deleting column
      this.deletingColumn = this.columnsDictionaries[index].getColumnName();
      // delete specified column
      this.columnsDictionaries.splice(index, 1);
      // trigger to ng after view checked
      this.columnsDictionariesLoaded = true;
    } else {
      // nothing to do because it is impossible here
    }
    // set deleting flag
    this.isDeletingColumn = true;
    // save all synonym rows
    this.saveColumnDictionaries();
  }
  // callback of edit synonym closed
  private callbackOfEditSynonymClosed(repsonse: string): void {
    // refresh all synonym table's row
    this.refreshAllSynonymTables();
  }
  // callback of add column  closed
  private callbackOfAddColumnClosed(addedColumn: string): void {
    // update columns after adding columns
    if (addedColumn !== null) {
      this.columns.push(addedColumn);
      this.columnsDictionariesLoaded = true;
    }
    // refresh all synonym table's row
    this.refreshAllSynonymTables();
    // refresh columns container service
    this.columnsContainer.refreshAllColumns();
  }
  // adjust modal options
  // if don't adjust modal options, modal will not be shown correctly
  adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    return options;
  }
  // delete synonym handler
  private deleteSynonymHandler(linkId: any): void {
    // clear info msg and error msg
    this.clearErrorMsg();
    this.clearInfoMsg();
    // separate id to three part for getting uuid and index
    // inde==0 method, modify/delete
    // index==1 uuid
    // index==2 index
    const idParts = linkId.split('_');
    // uuid
    const uuid = idParts[1];
    // index
    const index = Number(idParts[2]);
    // get column dictionary by uuid
    let deleteColumnDictionary: ColumnsDictionary = null;
    for (let i = 0; i < this.columnsDictionaries.length; i++) {
      const tempColumnDictionary = this.columnsDictionaries[i];
      if (tempColumnDictionary.getUUID() === uuid) {
        deleteColumnDictionary = tempColumnDictionary;
      }
    }
    if (deleteColumnDictionary !== null) {
      // save original column dictionaries
      this.saveOriginalColumnsDictionaries(
        this.EDIT_COLUMN,
        deleteColumnDictionary
      );
      deleteColumnDictionary.getSynonyms().splice(index, 1);
    }
    // if last synonym deleted, add delete column link
    if (deleteColumnDictionary.getSynonyms().length === 0) {
      deleteColumnDictionary.getSynonyms().push(
        this.DELETE_COLUMN_NAME + deleteColumnDictionary.getUUID());
    }
    // trigger to ng after view checked
    this.columnsDictionariesLoaded = true;
    // set deleting flag
    this.isDeletingSynonym = true;
    // save all synonym rows
    this.saveColumnDictionaries();
  }
  // set operate formatter to synonyms
  public operateFormatter(value, row, index): string {
    // find uuid of synonyms
    const uuid = this.findUUIDOfSynonyms(value);
    if ('' === uuid) {
      // it is impossible, but if true, return value
      return value;
    } else {
      // generate id as %method%uuid%index
      const modifySynonymId = 'modify_' + uuid + '_' + index;
      const deleteSynonymId = 'delete_' + uuid + '_' + index;
      const deleteColumnId = this.DELETE_COLUMN_NAME + uuid;
      const addColumnId = 'addcolumn' + uuid;
      if (value === this.ADD_COLUMN_NAME) {
        return [
          '<div style="float:left">',
          '<a id=' + addColumnId + ' href="javascript:void()">',
          '<span class="glyphicon glyphicon-plus"></span>&nbsp&nbsp' + this.ADD_COLUMN_TITLE + '</a>',
          '</div>',
        ].join('');
      } else if (value.startsWith(this.DELETE_COLUMN_NAME)) {
        return [
          '<div style="float:left">',
          '<a id=' + deleteColumnId + ' href="javascript:void()">',
          '<span class="glyphicon glyphicon-trash"></span>&nbsp&nbsp' + this.DELETE_COLUMN_TITLE + '</a>',
          '</div>',
        ].join('');
      } else {
        return [
          '<div style="float:left">',
          '<a id=' + modifySynonymId + ' href="javascript:void()">' + value + '</a>',
          '</div>',
          '<div style="float:right">',
          '<a id=' + deleteSynonymId + ' class="remove" href="javascript:void(0)" title="Remove">',
          '<i class="glyphicon glyphicon-trash"></i>',
          '</a>',
          '</div>'
        ].join('');
      }
    }
  }
  // find uuid of synonyms
  private findUUIDOfSynonyms(synonymTarget: string): string {
    if (synonymTarget.startsWith(this.DELETE_COLUMN_NAME)) {
      // if it starts with add_column, it must be the formula of delete_column_%uuid%
      // so after splitting, return idPart[1]
      const idParts = synonymTarget.split('_');
      return idParts[1];
    }
    let uuid = '';
    for (let i = 0; i < this.columnsDictionaries.length; i++) {
      const element = this.columnsDictionaries[i];
      for (let ii = 0; ii < element.getSynonyms().length; ii++) {
        const synonym = element.getSynonyms()[ii];
        if (synonymTarget === synonym) {
          uuid = element.getUUID();
          break;
        }
      }
    }
    return uuid;
  }
  // update synonym dictionaries
  protected updateColumnDictionaries(): void {
    // find column dictionary entry
    this.columnsDictionaries.forEach(element => {
      if (element.getColumnName() === this.column) {
        // save original column dictionaries
        this.saveOriginalColumnsDictionaries(
          this.EDIT_COLUMN,
          element
        );
        // if there is just one synonyms and this
        // synonym is delete column, then push this synonym
        // before delete column link
        if (element.getSynonyms().length === 1 &&
          element.getSynonyms()[0].startsWith(this.DELETE_COLUMN_NAME)) {
          element.getSynonyms().pop();
          element.getSynonyms().push(this.synonym);
        } else {
          element.getSynonyms().push(this.synonym);
        }
      }
    });
    // tigger to ng after view checked
    this.columnsDictionariesLoaded = true;
  }
  private setSelectOptions(id: string): void {
    $(id).selectpicker({
      'liveSearch': true,
    });
    $(id).selectpicker('val', '');
    $(id).selectpicker('refresh');
  }
  // save column dictionary end notification
  public callbackOnSaveEnd(httpResponse: HttpResponse): void {
    if (!(this.isDeletingSynonym === true ||
      this.isDeletingColumn === true)) {
      super.callbackOnSaveEnd(httpResponse);
    } else {
      if (httpResponse.code === 201) {
        this.currentUserContainer.sessionTimeout();
      } else if (httpResponse.code === 200) {
        // show ok msg
        if (this.isDeletingColumn) {
          this.infoExist = true;
          this.infoMsg = this.DELETE_COLUMN_OK;
          // update columns after deleting column
          this.deleteColumnFromColumnSelections();
          // set flag to callback ng view checked
          this.columnsDictionariesLoaded = true;
          // refresh columns container
          this.columnsContainer.refreshAllColumns();
        } else if (this.isDeletingSynonym) {
          this.infoExist = true;
          this.infoMsg = this.DELETE_SYNONYM_OK;
        } else {
          // nothing to do
        }
      } else {
        this.errorExist = true;
        this.errorMsg = httpResponse.message;
        // recover original columns dictionaries
        this.recoverOriginalColumnsDictionaries();
      }
      // set back deleting flag
      this.isDeletingSynonym = false;
      this.isDeletingColumn = false;
    }
    // set flag to callback ng view checked
    this.columnsDictionariesLoaded = true;
  }
  // set upload data error msg
  public setUploadDataErrorMsg(errorMsg: string): void {
    this.uploadErrorMsg = errorMsg;
  }
  // get upload data error msg
  public getUploadDataErrorMsg(): string {
    return this.uploadErrorMsg;
  }
  // check whether error msg is null or not
  public isUploadDataErrorMsgEmpty(): boolean {
    if (this.uploadErrorMsg === null || this.uploadErrorMsg === '') {
      return true;
    } else {
      return false;
    }
  }
  // delete column from columns selections
  private deleteColumnFromColumnSelections(): void {
    let index = -1;
    for (let i = 0; i < this.columns.length; i++) {
      if (this.columns[i] === this.deletingColumn) {
        index = i;
        break;
      }
    }
    // delete column
    if (index !== -1) {
      this.columns.splice(index, 1);
    }
  }
}
