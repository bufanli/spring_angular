import { Injectable } from '@angular/core';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { NgbModal, NgbModalOptions, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { DataSelectHeadersComponent } from '../components/data-select-headers/data-select-headers.component';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SaveHeaderDisplaysCallback } from '../entities/save-header-displays-callback';
import { Observable } from 'rxjs';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class DataHeaderDisplayService {
  // statistics settings url
  private getHeadersForSettingUrl = 'api/getHeadersForSetting';  // URL to web api
  private setHeadersForSettingUrl = 'api/setHeadersForSetting';  // URL to web api
  private GET_HEADERS_FOR_SETTING_SOURCE_ID = '001';
  // set save end callback
  private saveEndCallbacks: SaveHeaderDisplaysCallback[] = [];
  constructor(private commonUtilitiesService: CommonUtilitiesService,
    private http: HttpClient,
    public modalService: NgbModal,
    private currentUserContainer: CurrentUserContainerService) {
  }
  // get header for setting
  public getHeadersForSetting(): void {
    this.commonUtilitiesService.showProcessingDialog(this,
      null,
      this.GET_HEADERS_FOR_SETTING_SOURCE_ID);
  }
  // callback on processing
  public callbackOnProcessing(sourceID: string, data: any): void {
    if (sourceID === this.GET_HEADERS_FOR_SETTING_SOURCE_ID) {
      // post get statistics fields request
      this.http.get<HttpResponse>(this.getHeadersForSettingUrl).subscribe(
        httpResponse => { this.callbackGettingHeaderForSetting(httpResponse); }
      );
    }
  }
  // callback when getting statistics fields
  private callbackGettingHeaderForSetting(httpResponse: HttpResponse) {
    if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
      return;
    }
    // close processing dialog
    this.commonUtilitiesService.closeProcessingDialog();
    const service: NgbModal = this.modalService;
    // you can not call this.adjustModalOptions,
    // because showUserSettingModal called in html context
    const modalRef = service.open(DataSelectHeadersComponent, this.adjustModalOptions());
    // set statistics fields to statistics component
    modalRef.componentInstance.setHeaderDisplays(httpResponse.data);
    // save service
    modalRef.componentInstance.setHeaderDisplaysService(this);
  }
  // adjust modal options
  // if don't adjust modal options, modal will not be shown correctly
  private adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    options.size = 'lg';
    return options;
  }
  // set callback of save headers
  public appendSaveEndCallback(saveHeaderDisplayCallback: SaveHeaderDisplaysCallback): void {
    this.saveEndCallbacks.push(saveHeaderDisplayCallback);
  }
  // save header displays
  public saveHeaderDisplays(headerDisplays: any[]): void {
    this.saveHeaderDisplaysImpl(headerDisplays).subscribe
      (httpResponse => this.saveHeaderDisplaysNotification(httpResponse));
  }
  // save synonym dictionaries implementation
  private saveHeaderDisplaysImpl(headerDisplays: any[]): Observable<HttpResponse> {
    return this.http.post<HttpResponse>(
      this.setHeadersForSettingUrl,
      headerDisplays,
      httpOptions);
  }
  // notification of saving header displays
  private saveHeaderDisplaysNotification(httpResponse: HttpResponse): void {
    this.saveEndCallbacks.forEach(element => {
      element.callbackOnEndSaveHeaderDisplays(httpResponse);
    });
  }
}
