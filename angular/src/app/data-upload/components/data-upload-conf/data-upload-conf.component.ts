import { Component, OnInit, ViewChild } from '@angular/core';
import { DataUploadComponent } from '../data-upload/data-upload.component';
import { EditDictionaryComponent } from '../edit-dictionary/edit-dictionary.component';
import { DataDictionaryComponent } from '../data-dictionary/data-dictionary.component';
enum ShowType {
  SHOW_SYNONYM_EDIT,
  SHOW_DATA_UPLOAD,
  SHOW_DATA_DICTIONARY_EDIT,
}
@Component({
  selector: 'app-data-upload-conf',
  templateUrl: './data-upload-conf.component.html',
  styleUrls: ['./data-upload-conf.component.css']
})

export class DataUploadConfComponent implements OnInit {

  // showing tab type
  public showType: ShowType = ShowType.SHOW_DATA_UPLOAD;
  constructor() { }

  @ViewChild('dataUpload')
  dataUploadComponent: DataUploadComponent;
  @ViewChild('editDictionary')
  editDictionaryComponent: EditDictionaryComponent;
  @ViewChild('editDataDictionary')
  editDataDictionaryComponent: DataDictionaryComponent;

  ngOnInit() {
    this.dataUploadComponent.notifyOpenSynonym.subscribe(response =>
      this.openSynonymEdit(response));
    this.editDataDictionaryComponent.notifyOpenSynonym.subscribe(response =>
      this.openSynonymEdit(response));
  }

  // open synonym edit
  private openSynonymEdit(errorMsg: string) {
    // show synonym edit tab
    this.showType = ShowType.SHOW_SYNONYM_EDIT;
    // set error msg to synonym edit tab
    this.editDictionaryComponent.setUploadDataErrorMsg(errorMsg);
  }
  // set synonym edit active
  private setShowType(showType: ShowType): void {
    this.showType = showType;
  }
  // show data upload
  public showDataUpload(): void {
    this.setShowType(ShowType.SHOW_DATA_UPLOAD);
  }
  // show synonym edit
  public showSynonymEdit(): void {
    this.setShowType(ShowType.SHOW_SYNONYM_EDIT)
  }
  // show data dictionary edit
  public showDataDictionaryEdit(): void {
    this.setShowType(ShowType.SHOW_DATA_DICTIONARY_EDIT);
  }
}
