import { Component } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';

// const URL = '/api/';
const URL = 'https://evening-anchorage-3159.herokuapp.com/api/';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload-component.html',
  styleUrls: ['./file-upload-component.css']
})
export class FileUploadComponent {
  public uploader: FileUploader = new FileUploader({url: URL});
}
