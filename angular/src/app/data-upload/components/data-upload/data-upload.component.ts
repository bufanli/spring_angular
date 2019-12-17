import { Component, AfterViewChecked, Output, EventEmitter } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { FileItem } from 'ng2-file-upload';
import { ParsedResponseHeaders } from 'ng2-file-upload';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { HttpClient } from '@angular/common/http';
import { HttpResponse } from '../../../common/entities/http-response';
import { Observable } from 'rxjs';

const URL = 'api/uploadFile';
const DELETE_SAME_DATA_URL = 'api/deleteSameData';

@Component({
  selector: 'app-file-upload',
  templateUrl: './data-upload.component.html',
  styleUrls: ['./data-upload.component.css']
})
export class DataUploadComponent implements AfterViewChecked {
  @Output() notifyOpenSynonym: EventEmitter<string> = new EventEmitter<string>();
  public uploader: FileUploader = new FileUploader({ url: URL });
  constructor(private currentUserContainer: CurrentUserContainerService,
    private http: HttpClient) {
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
          const extraData = result.data;
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
    this.uploader.onCompleteAll = function () {
      // get query conditions caller
      // delete same when uploading 10000 data records
      // so it is not necessary to delete same data after all completed
      // that.deleteSameData();
    };
  }
  private deleteSameData(): void {
    this.deleteSameDataImpl().subscribe(
      httpResponse => {// nothing to do
      });
  }
  // delete same data implementation
  private deleteSameDataImpl(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(DELETE_SAME_DATA_URL);
  }
  // show tooltip when completing to upload file
  ngAfterViewChecked() {
    $('[data-toggle="tooltip"]').each(function () {
      $(this).tooltip();
    });
  }
  // click file select button
  selectFile(): void {
    // tslint:disable-next-line: deprecation
    $('#file-select').click();
  }
  // emit synonym edit event
  public synonymEdit(item: any): void {
    this.notifyOpenSynonym.emit(item.formData['failedDetail']);
  }
}
