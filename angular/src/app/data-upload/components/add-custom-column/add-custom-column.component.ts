import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SaveColumnSynonymsService } from '../../services/save-column-synonyms.service';
import { ColumnsDictionary } from '../../entities/columns-dictionary';
import { EditSynonymBase } from '../../interfaces/edit-synonym-base';

@Component({
  selector: 'app-add-custom-column',
  templateUrl: './add-custom-column.component.html',
  styleUrls: ['./add-custom-column.component.css']
})
export class AddCustomColumnComponent extends EditSynonymBase implements OnInit {
  public readonly CUSTOM_COLUMN_NAME = '自定义列';
  public readonly CUSTOM_COLUMN_TITLE = '请添加自定义列';
  public readonly CUSTOM_COLUMN_EMPTY = '自定义列为空,请输入自定义列';
  public readonly CUSTOM_COLUMN_EXIST = '该自定义列已经存在';
  public readonly COSTOM_COLUMN_SAVED = '该自定义列已经保存';
  private readonly DELETE_COLUMN_NAME = 'deletecolumn_';
  // notify close event
  @Output() notifyClose: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    protected currentUserContainer: CurrentUserContainerService,
    private activeModal: NgbActiveModal,
    protected saveColumnSynonymService: SaveColumnSynonymsService) {
    super(currentUserContainer, saveColumnSynonymService);
  }
  ngOnInit() {
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
    for (let i = 0; i < this.columnsDictionaries.length; i++) {
      if (this.columnsDictionaries[i].getColumnName() === this.column) {
        return true;
      }
    }
    return false;
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
    // save original column dictionaries before updating it
    this.saveOriginalColumnsDictionaries()
    // update column dictionaries
    this.updateColumnDictionaries();
    // set column dictionaries
    this.saveColumnSynonymService.setColumnDictionaries(this.columnsDictionaries);
    // set save end callback
    this.saveColumnSynonymService.setCallback(this);
    // save column dictionary
    this.saveColumnSynonymService.saveColumnDictionaries();
  }
  // update synonym dictionaries
  protected updateColumnDictionaries(): void {
    // update column dictionaries
    // 1. popup the last column dictionary
    const lastColumnDictionary = this.columnsDictionaries.pop();
    // 2. push the new custom column
    const customColumn = new ColumnsDictionary(
      this.column,
      // add delete column link because it must have no synonym in that time
      [this.DELETE_COLUMN_NAME]);
    this.columnsDictionaries.push(customColumn);
    // 3. push back the last element
    this.columnsDictionaries.push(lastColumnDictionary);
  }
}
