import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { ColumnsDictionary } from '../../entities/columns-dictionary';

@Component({
  selector: 'app-edit-synonym',
  templateUrl: './edit-synonym.component.html',
  styleUrls: ['./edit-synonym.component.css']
})
export class EditSynonymComponent implements OnInit {
  public readonly SYNONYM_NAME = '同义词';
  public readonly COLUMN_NAME = '原词';
  public readonly SYNONYM_IS_EMPTY = '同义词为空,请输入同义词';
  public readonly SYNONYM_IS_DUPLICATED = '该同义词已经定义,原词为 ';
  public readonly SYNONYM_IS_SAVED = '该同义词保存';
  // column dictionaries
  private columnsDictionaries: ColumnsDictionary[] = null;
  // edit synonym index
  private editSynonymIndex = 0;
  // uuid
  private uuid = null;
  // synonym
  public synonym: string = null;
  // column
  public column: string = null;
  // synonym error
  public errorExist = false;
  public errorMsg: string = null;
  // synonym error
  public infoExist = false;
  public infoMsg: string = null;

  @Output() notifyClose: EventEmitter<string> = new EventEmitter<string>();

  constructor() { }

  ngOnInit() {
  }
  // set column dictionaries getting from parent component
  public setColumnsDictionaries(columnsDictionaries: ColumnsDictionary[]): void {
    this.columnsDictionaries = columnsDictionaries;
  }
  // set editing synonym index
  public setEditSynonymIndex(index: number): void {
    this.editSynonymIndex = index;
  }
  // set editing column name
  public setUUID(uuid: string): void {
    this.uuid = uuid;
  }
  // on edit synonym
  public onEditSynonym(): void {
    // clear error and info
    this.clearErrorMsg();
    this.clearInfoMsg();
    // error check
    if (this.synonym === '') {
      this.errorMsg = this.SYNONYM_IS_EMPTY;
      this.errorExist = true;
      return;
    } else {
      const column = this.synonymExist(this.synonym);
      if (column !== null) {
        this.errorExist = true;
        this.errorMsg = this.SYNONYM_IS_DUPLICATED + column;
        return;
      } else {
        // no duplicated synonym exists
        this.updateColumnDictionaries();
        // save synonym dictionaries
        this.saveColumnDictionaries();
      }
    }
  }
  // save synonym dictionaries
  private saveColumnDictionaries(): void {
    // todo
  }
  // update synonym dictionaries
  private updateColumnDictionaries(): void {
    // get synonym dictionary entry by uuid
    let columnsDictionary: ColumnsDictionary = null;
    this.columnsDictionaries.forEach(element => {
      if (element.getUUID() === this.uuid) {
        columnsDictionary = element;
      }
    });
    if (columnsDictionary === null) {
      // it is impossible here
      return;
    } else {
      const synonyms: string[] = columnsDictionary.getSynonyms();
      synonyms[this.editSynonymIndex] = this.synonym;
    }
  }
  // tell whether synonym exist or not
  private synonymExist(synonymInput: string): string {
    this.columnsDictionaries.forEach(element => {
      element.getSynonyms().forEach(synonym => {
        if (synonymInput === synonym) {
          return element;
        }
      });
    });
    return null;
  }
  // on entering synonym
  public onEnterSynonym(event: any): void {
    this.clearErrorMsg();
    this.clearInfoMsg();
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
}
