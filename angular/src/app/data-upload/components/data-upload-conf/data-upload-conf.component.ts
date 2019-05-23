import { Component, OnInit, ViewChild } from '@angular/core';
import { DataUploadComponent } from '../data-upload/data-upload.component';
import { EditDictionaryComponent } from '../edit-dictionary/edit-dictionary.component';

@Component({
  selector: 'app-data-upload-conf',
  templateUrl: './data-upload-conf.component.html',
  styleUrls: ['./data-upload-conf.component.css']
})
export class DataUploadConfComponent implements OnInit {

  constructor() { }

  @ViewChild('dataUpload')
  dataUploadComponent: DataUploadComponent;
  @ViewChild('editDictionary')
  editDictionaryComponent: EditDictionaryComponent;

  ngOnInit() {
  }


}
