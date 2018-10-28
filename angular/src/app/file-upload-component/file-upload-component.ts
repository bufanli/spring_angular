import { Component } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { FileItem } from 'ng2-file-upload';
import { ParsedResponseHeaders } from 'ng2-file-upload';

const URL = 'uploadFile';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload-component.html',
  styleUrls: ['./file-upload-component.css']
})
export class FileUploadComponent {
  public uploader: FileUploader = new FileUploader({ url: URL });

  constructor() {
    this.uploader.onCompleteItem = this.successItem;
  }

  successItem(item: FileItem, response: string, status: number, headers: ParsedResponseHeaders): any {
    // success
    if (status === 200) {
      const res = JSON.parse(response);
      alert(item._file.name);
    } else {
      // failed
    }
  }
}
