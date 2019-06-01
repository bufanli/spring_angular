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

@Component({
  selector: 'app-edit-dictionary',
  templateUrl: './edit-dictionary.component.html',
  styleUrls: ['./edit-dictionary.component.css']
})
export class EditDictionaryComponent extends EditSynonymBase implements OnInit, AfterViewChecked {
  public readonly fieldName = 'synonym';
  private readonly getColumnDictionaryURL = 'api/getColumnsDictionary';
  public columnsDictionaries: ColumnsDictionary[] = null;
  public columns: string[] = null;
  private columnsDictionariesLoaded = false;
  private isDeletingSynonym = false;
  // id is a dummy attribute, just for compilation
  private id: any;
  constructor(private http: HttpClient,
    protected currentUserContainer: CurrentUserContainerService,
    protected saveColumnSynonymsService: SaveColumnSynonymsService,
    public modalService: NgbModal) {
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
  // callback of edit synonym closed
  private callbackOfEditSynonymClosed(repsonse: string): void {
    // refresh all synonym table's row
    this.refreshAllSynonymTables();
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
      deleteColumnDictionary.getSynonyms().splice(index, 1);
    }
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
  // find uuid of synonyms
  private findUUIDOfSynonyms(synonymTarget: string): string {
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
        element.getSynonyms().push(this.synonym);
      }
    });
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
    if (this.isDeletingSynonym === false) {
      super.callbackOnSaveEnd(httpResponse);
    } else {
      if (httpResponse.code === 201) {
        this.currentUserContainer.sessionTimeout();
      } else if (httpResponse.code === 200) {
        // nothing to do
      } else {
        this.errorExist = true;
        this.errorMsg = httpResponse.message;
      }
      // refresh synonyms row
      this.refreshAllSynonymTables();
      // set back deleting flag
      this.isDeletingSynonym = false;
    }
  }
}
