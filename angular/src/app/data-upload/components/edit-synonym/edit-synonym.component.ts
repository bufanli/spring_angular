import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { ColumnsDictionary } from '../../entities/columns-dictionary';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { Observable } from 'rxjs';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SaveColumnSynonymsService } from '../../services/save-column-synonyms.service';
import { SaveColumnDictionaryCallback } from '../../interfaces/save-column-dictionary-callback';
import { EditSynonymBase } from '../../interfaces/edit-synonym-base';



@Component({
  selector: 'app-edit-synonym',
  templateUrl: './edit-synonym.component.html',
  styleUrls: ['./edit-synonym.component.css']
})
export class EditSynonymComponent extends EditSynonymBase implements OnInit, SaveColumnDictionaryCallback {
  // edit synonym index
  private editSynonymIndex = 0;
  // uuid
  private uuid = null;

  @Output() notifyClose: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    protected currentUserContainer: CurrentUserContainerService,
    private activeModal: NgbActiveModal,
    protected saveColumnSynonymService: SaveColumnSynonymsService) {
    super(currentUserContainer, saveColumnSynonymService);
  }

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
  // refresh data model after set column dictionary and index
  // this method should be called after setEditSynonymIndex and setColumnsDictionaries
  public refreshDataModel(): void {
    let columnDictionary: ColumnsDictionary = null;
    for (let i = 0; i < this.columnsDictionaries.length; i++) {
      if (this.columnsDictionaries[i].getUUID() === this.uuid) {
        columnDictionary = this.columnsDictionaries[i];
        break;
      }
    }
    if (columnDictionary === null) {
      // it is impossible here
      return;
    } else {
      this.column = columnDictionary.getColumnName();
      this.synonym = columnDictionary.getSynonyms()[this.editSynonymIndex];
    }
  }
  // close modal
  public close(): void {
    this.activeModal.close();
  }
  // update synonym dictionaries
  protected updateColumnDictionaries(): void {
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
}
