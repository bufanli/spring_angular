import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SaveColumnSynonymsService } from '../../services/save-column-synonyms.service';
import { ColumnsDictionary } from '../../entities/columns-dictionary';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { last } from '@angular/router/src/utils/collection';

@Component({
  selector: 'app-add-custom-column',
  templateUrl: './add-custom-column.component.html',
  styleUrls: ['./add-custom-column.component.css']
})
export class AddCustomColumnComponent implements OnInit {
  public readonly CUSTOM_COLUMN_NAME = '自定义列';
  public readonly CUSTOM_COLUMN_TITLE = '请添加自定义列';
  public readonly CUSTOM_COLUMN_EMPTY = '自定义列为空,请输入自定义列';
  public readonly CUSTOM_COLUMN_EXIST = '该自定义列已经存在';
  public readonly COSTOM_COLUMN_SAVED = '该自定义列已经保存';
  private readonly DELETE_COLUMN_NAME = 'delete_column_';
  // column
  public column: string = null;
  // synonym error
  public errorExist = false;
  public errorMsg: string = null;
  // synonym info
  public infoExist = false;
  public infoMsg: string = null;
  // column dictionaries
  public columnDictionaries: ColumnsDictionary[] = null;
  // notify close event
  @Output() notifyClose: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    protected currentUserContainer: CurrentUserContainerService,
    private activeModal: NgbActiveModal,
    protected saveColumnSynonymService: SaveColumnSynonymsService) {
  }
  ngOnInit() {
  }
  // set column dictionaries getting from parent component
  public setColumnsDictionaries(columnsDictionaries: ColumnsDictionary[]): void {
    this.columnDictionaries = columnsDictionaries;
  }
  // close modal
  public close(): void {
    this.activeModal.close();
    // notify close
    this.notifyClose.emit('column_added');
  }
  // on entering synonym
  public onEnterCustomColumn(event: any): void {
    this.clearErrorMsg();
    this.clearInfoMsg();
  }
  // check whether column exist or not
  public columnExists(): boolean {
    for (let i = 0; i < this.columnDictionaries.length; i++) {
      if (this.columnDictionaries[i].getColumnName() === this.column) {
        return true;
      }
    }
    return false;
  }
  // clear info msg
  private clearInfoMsg(): void {
    this.infoExist = false;
    this.infoMsg = '';
  }
  // clear error msg
  private clearErrorMsg(): void {
    this.errorExist = false;
    this.errorMsg = '';
  }
  // save synonym dictionaries
  public saveColumnDictionaries(): void {
    // check whether input column is empty or not
    if (this.column === '') {
      this.errorExist = true;
      this.errorMsg = this.CUSTOM_COLUMN_EMPTY;
      return;
    }
    // check whether column exists or not
    if (this.columnExists() === true) {
      this.errorExist = true;
      this.errorMsg = this.CUSTOM_COLUMN_EXIST;
      return;
    }
    // update column dictionaries
    this.updateColumnDictionaries();
    // set column dictionaries
    this.saveColumnSynonymService.setColumnDictionaries(this.columnDictionaries);
    // set save end callback
    this.saveColumnSynonymService.setCallback(this);
    // save column dictionary
    this.saveColumnSynonymService.saveColumnDictionaries();
  }
  // save column dictionary notification
  public callbackOnSaveEnd(httpResponse: HttpResponse): void {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    } else if (httpResponse.code === 200) {
      this.infoExist = true;
      this.infoMsg = this.COSTOM_COLUMN_SAVED;
    } else {
      this.errorExist = true;
      this.errorMsg = httpResponse.message;
    }
  }
  // update synonym dictionaries
  private updateColumnDictionaries(): void {
    // update column dictionaries
    // 1. popup the last column dictionary
    const lastColumnDictionary = this.columnDictionaries.pop();
    // 2. push the new custom column
    const customColumn = new ColumnsDictionary(
      this.column,
      // add delete column link because it must have no synonym in that time
      [this.DELETE_COLUMN_NAME]);
    this.columnDictionaries.push(customColumn);
    // 3. push back the last element
    this.columnDictionaries.push(lastColumnDictionary);
  }
}
