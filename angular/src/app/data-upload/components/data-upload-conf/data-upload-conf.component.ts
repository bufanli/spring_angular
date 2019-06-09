import { Component, OnInit, ViewChild } from '@angular/core';
import { DataUploadComponent } from '../data-upload/data-upload.component';
import { EditDictionaryComponent } from '../edit-dictionary/edit-dictionary.component';

@Component({
  selector: 'app-data-upload-conf',
  templateUrl: './data-upload-conf.component.html',
  styleUrls: ['./data-upload-conf.component.css']
})
export class DataUploadConfComponent implements OnInit {
  public isSynonymEditActive = false;
  constructor() { }

  @ViewChild('dataUpload')
  dataUploadComponent: DataUploadComponent;
  @ViewChild('editDictionary')
  editDictionaryComponent: EditDictionaryComponent;

  ngOnInit() {
    this.dataUploadComponent.notifyOpenSynonym.subscribe(response =>
      this.openSynonymEdit(response));
  }
  // open synonym edit
  private openSynonymEdit(errorMsg: string) {
    // show synonym edit tab
    this.isSynonymEditActive = true;
    // set error msg to synonym edit tab
    this.editDictionaryComponent.setUploadDataErrorMsg(errorMsg);
  }
  // set synonym edit active
  public setSynonymEditActive(isSynonymEditActive: boolean): void {
    this.isSynonymEditActive = isSynonymEditActive;
  }

}
