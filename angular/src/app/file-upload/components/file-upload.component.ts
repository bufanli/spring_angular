import { Component, AfterViewChecked } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { FileItem } from 'ng2-file-upload';
import { ParsedResponseHeaders } from 'ng2-file-upload';
import { UploadStatus } from '../entities/upload-status';

const URL = 'uploadFile';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements AfterViewChecked {
  public uploader: FileUploader = new FileUploader({ url: URL });
  constructor() {
    this.uploader.onCompleteItem = this.successItem;
  }
  successItem(item: FileItem, response: string, status: number, headers: ParsedResponseHeaders): any {
    // success
    if (status === 200) {
      // convert string to json
      const result = JSON.parse(response);
      // get code from response
      const code = result.code;
      let isUploadSucceeded = false;
      if (code === 200) {
        isUploadSucceeded = true;
      } else {
        isUploadSucceeded = false;
      }
      // get message from response
      const message = result.message;
      let fileName = null;
      let failedDetail = null;
      // analyse extra data
      if (result.data != null) {
        const extraData = result.data[0];
        const index = extraData.indexOf(':');
        if (index !== -1) {
          fileName = extraData.substring(0, index);
          failedDetail = extraData.substring(index + 1);
        } else {
          fileName = extraData;
          failedDetail = '';
        }
      } else {
        fileName = '';
        failedDetail = '';
      }
      // compose result to item
      item.formData = {
        'upload_succeeded': isUploadSucceeded,
        'summary': message,
        'failedDetail': failedDetail
      };
    } else {
      // failed
      // nothing to do
    }
  }
  // show tooltip when completing to upload file
  ngAfterViewChecked() {
    $('[data-toggle="tooltip"]').each(function () {
      $(this).tooltip();
    });
  }
  // click file select button
  selectFile(): void {
    $('#file-select').click();
  }
}
