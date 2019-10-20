import { Component, OnInit, ViewChild } from '@angular/core';
import { DataUploadComponent } from '../data-upload/data-upload.component';
import { EditDictionaryComponent } from '../edit-dictionary/edit-dictionary.component';
import { DataDictionaryComponent } from '../data-dictionary/data-dictionary.component';
@Component({
  selector: 'app-data-upload-conf',
  templateUrl: './data-upload-conf.component.html',
  styleUrls: ['./data-upload-conf.component.css']
})

export class DataUploadConfComponent implements OnInit {

  // showing tab type
  public showType = 0;
  private readonly SHOW_DATA_UPLOAD = 1;
  private readonly SHOW_SYNONYM_EDIT = 2;
  private readonly SHOW_DATA_DICTIONARY_EDIT = 3;
  constructor() { }

  @ViewChild('dataUpload')
  dataUploadComponent: DataUploadComponent;
  @ViewChild('editDictionary')
  editDictionaryComponent: EditDictionaryComponent;
  @ViewChild('editDataDictionary')
  editDataDictionaryComponent: DataDictionaryComponent;

  ngOnInit() {
    this.showType = this.SHOW_DATA_UPLOAD;
    this.dataUploadComponent.notifyOpenSynonym.subscribe(response =>
      this.openSynonymEdit(response));
    this.editDataDictionaryComponent.notifyOpenSynonym.subscribe(response =>
      this.openSynonymEdit(response));
  }

  // open synonym edit
  private openSynonymEdit(errorMsg: string) {
    // show synonym edit tab
    this.showType = this.SHOW_SYNONYM_EDIT;
    // set error msg to synonym edit tab
    this.editDictionaryComponent.setUploadDataErrorMsg(errorMsg);
  }
  // set synonym edit active
  private setShowType(showType): void {
    this.showType = showType;
  }
  // show data upload
  public showDataUpload(): void {
    this.setShowType(this.SHOW_DATA_UPLOAD);
  }
  // show synonym edit
  public showSynonymEdit(): void {
    this.setShowType(this.SHOW_SYNONYM_EDIT);
  }
  // show data dictionary edit
  public showDataDictionaryEdit(): void {
    this.setShowType(this.SHOW_DATA_DICTIONARY_EDIT);
  }
}
