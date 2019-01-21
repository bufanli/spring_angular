import { Component, AfterViewChecked } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { FileItem } from 'ng2-file-upload';
import { ParsedResponseHeaders } from 'ng2-file-upload';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';

const URL = 'api/uploadFile';

@Component({
  selector: 'app-file-upload',
  templateUrl: './data-upload.component.html',
  styleUrls: ['./data-upload.component.css']
})
export class DataUploadComponent implements AfterViewChecked {
  public uploader: FileUploader = new FileUploader({ url: URL });
  constructor(private currentUserContainer: CurrentUserContainerService) {
    const that: any = this;
    this.uploader.onCompleteItem = function (item: FileItem,
      response: string, status: number, headers: ParsedResponseHeaders) {
      // success
      if (status === 200) {
        // convert string to json
        const result = JSON.parse(response);
        // get code from response
        const code = result.code;
        let isUploadSucceeded = false;
        if (code === 200) {
          isUploadSucceeded = true;
        } else if (code === 201) {
          that.currentUserContainer.sessionTimeout();
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
    };
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
