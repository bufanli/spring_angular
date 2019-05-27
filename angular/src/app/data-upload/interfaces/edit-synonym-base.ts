import { ColumnsDictionary } from '../entities/columns-dictionary';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { SaveColumnSynonymsService } from '../services/save-column-synonyms.service';
import { HttpResponse } from 'src/app/common/entities/http-response';

export abstract class EditSynonymBase {
  public readonly SYNONYM_NAME = '同义词';
  public readonly SYNONYM_EDIT_TITLE = '请编辑同义词';
  public readonly COLUMN_NAME = '原词';
  public readonly SYNONYM_IS_EMPTY = '同义词为空,请输入同义词';
  public readonly COLUMN_IS_EMPTY = '原词为空,请选择原词';
  public readonly SYNONYM_IS_DUPLICATED = '该同义词已经定义,原词为 ';
  public readonly SYNONYM_IS_SAVED = '该同义词已经保存';
  // synonym
  public synonym: string = null;
  // column
  public column: string = null;
  // synonym error
  public errorExist = false;
  public errorMsg: string = null;
  // synonym info
  public infoExist = false;
  public infoMsg: string = null;
  // column dictionaries
  protected columnsDictionaries: ColumnsDictionary[] = null;
  constructor(
    protected currentUserContainer: CurrentUserContainerService,
    protected saveColumnSynonymService: SaveColumnSynonymsService) { }
  // on edit synonym
  public onSaveSynonyms(): void {
    // clear error and info
    this.clearErrorMsg();
    this.clearInfoMsg();
    // error check
    if (this.synonym === '' || this.synonym === null) {
      this.errorMsg = this.SYNONYM_IS_EMPTY;
      this.errorExist = true;
      return;
    } else if (this.column === '' || this.column === null) {
      this.errorMsg = this.COLUMN_IS_EMPTY;
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
  protected abstract updateColumnDictionaries(): void;
  // save synonym dictionaries
  private saveColumnDictionaries(): void {
    // set column dictionaries
    this.saveColumnSynonymService.setColumnDictionaries(this.columnsDictionaries);
    // set save end callback
    this.saveColumnSynonymService.setCallback(this);
  }
  // save column dictionary notification
  public callbackOnSaveEnd(httpResponse: HttpResponse): void {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    } else if (httpResponse.code === 200) {
      this.infoExist = true;
      this.infoMsg = this.SYNONYM_IS_SAVED;
    } else {
      this.errorExist = true;
      this.errorMsg = httpResponse.message;
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
