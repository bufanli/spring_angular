import { Component, AfterViewChecked, Output, EventEmitter } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { FileItem } from 'ng2-file-upload';
import { ParsedResponseHeaders } from 'ng2-file-upload';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { HttpClient } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

const URL = 'api/importDataDictionary';

@Component({
  selector: 'app-data-dictionary-upload',
  templateUrl: './data-dictionary-upload.component.html',
  styleUrls: ['./data-dictionary-upload.component.css']
})
export class DataDictionaryUploadComponent implements AfterViewChecked {
  @Output() notifyOpenSynonym: EventEmitter<string> = new EventEmitter<string>();
  // dictionary name which is imported
  private dictionaryName: string = null;
  public dataDictionaryUploader: FileUploader = new FileUploader({ url: URL });
  constructor(
    private currentUserContainer: CurrentUserContainerService,
    private http: HttpClient,
    private activeModal: NgbActiveModal) {
    const that: any = this;
    this.dataDictionaryUploader.onAfterAddingFile = function (item: FileItem) {
      that.dataDictionaryUploader.clearQueue();
      that.dataDictionaryUploader.queue.push(item);
    };
    this.dataDictionaryUploader.onBuildItemForm = function (item: FileItem, form: any) {
      form.append('dictionaryName', that.dictionaryName);
    };
    this.dataDictionaryUploader.onCompleteItem = function (item: FileItem,
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
  }
  // show tooltip when completing to upload file
  public ngAfterViewChecked() {
    $('[data-toggle="tooltip"]').each(function () {
      $(this).tooltip();
    });
  }
  // click file select button
  public selectFile(): void {
    // tslint:disable-next-line: deprecation
    $('#data-dictionary-file-select').click();
  }
  // import dictionary
  public importDictionary(item: FileItem): void {
    item.upload();
  }
  // set dictionary name
  public setDictionaryName(dictionaryName: string): void {
    this.dictionaryName = dictionaryName;
  }
  // add custom column
  public addCustomColumn(item: FileItem): void {
    this.notifyOpenSynonym.emit(item['failedDetail']);
  }
  // close modal
  public close(): void {
    this.activeModal.close();
  }
}
