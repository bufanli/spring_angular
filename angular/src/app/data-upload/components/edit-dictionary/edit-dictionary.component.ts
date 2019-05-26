import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { Observable } from 'rxjs';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { ColumnsDictionary } from '../../entities/columns-dictionary';
import { NgbModal, NgbModalOptions, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { EditSynonymComponent } from '../edit-synonym/edit-synonym.component';

@Component({
  selector: 'app-edit-dictionary',
  templateUrl: './edit-dictionary.component.html',
  styleUrls: ['./edit-dictionary.component.css']
})
export class EditDictionaryComponent implements OnInit, AfterViewChecked {
  private readonly getColumnsDictionaryURl = 'api/getColumnsDictionary';
  public readonly fieldName = 'synonym';
  public columnsDictionaries: ColumnsDictionary[] = null;
  private columnsDictionariesLoaded = false;
  // id is a dummy attribute, just for compilation
  private id: any;
  constructor(private http: HttpClient,
    private currentUserContainer: CurrentUserContainerService,
    public modalService: NgbModal) { }

  ngOnInit() {
    // get columns and synonyms
    this.getColumnsDictionaryImpl().subscribe(httpResponse =>
      this.getColumnsDictionaryNotification(httpResponse));
  }
  // get columns from server implementation
  private getColumnsDictionaryImpl(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getColumnsDictionaryURl);
  }
  private getColumnsDictionaryNotification(httpResponse: HttpResponse): void {
    if (httpResponse.code === 200) {
      // get columns dictionary ok
      const columnsDictionaries: any = httpResponse.data;
      this.convertHttpDataToColumnsDictionaries(columnsDictionaries);
      // set loaded flag
      this.columnsDictionariesLoaded = true;
    } else if (httpResponse.code === 201) {
      this.currentUserContainer.sessionTimeout();
    }
  }
  // convert http data to columns
  private convertHttpDataToColumnsDictionaries(httpData: any): void {
    this.columnsDictionaries = [];
    httpData.forEach(element => {
      const columnsDictionary: ColumnsDictionary = new ColumnsDictionary(
        element.columnName, element.synonyms
      );
      this.columnsDictionaries.push(columnsDictionary);
    });
  }
  // abstract synonyms tables  row by given uuid
  private abstractSynonymRowsByUUID(uuid: string): any[] {
    const synonymsRows = [];
    this.columnsDictionaries.forEach(element => {
      if (uuid === element.getUUID()) {
        const synonyms = element.getSynonyms();
        synonyms.forEach(synonym => {
          const row = { synonym: synonym };
          synonymsRows.push(row);
        });
      }
    });
    return synonymsRows;
  }
  ngAfterViewChecked() {
    // load synonym tables
    if (this.columnsDictionariesLoaded === true) {
      this.columnsDictionaries.forEach(element => {
        $('#' + element.getUUID()).bootstrapTable({
          data: this.abstractSynonymRowsByUUID(element.getUUID()),
        });
      });
    } else {
      // nothing to do
    }
  }
  // bind click event to synonym row
  private bindClickEventToSynonym(): void {
    let index = 0;
    this.columnsDictionaries.forEach(element => {
      element.getSynonyms().forEach(synonym => {
        // generate id as %method%uuid%index
        const modifySynonymId = '#modify%' + element.getUUID() + '%' + index;
        const deleteSynonymId = '#delete%' + element.getUUID() + '%' + index;
        // bind modify synonym handler
        $(modifySynonymId).on('click', this, this.modifySynonymHandler);
        // bind delete synonym handler
        $(deleteSynonymId).on('click', this, this.deleteSynonymHandler);
      });
      index++;
    });
  }
  // modify synonym handler
  private modifySynonymHandler(target: any): void {
    // separate id to three part for getting uuid and index
    // inde==0 method, modify/delete
    // index==1 uuid
    // index==2 index
    const idParts = this.id.split('%');
    const component = target.data;
    const modalService: NgbModal = component.modalService;
    const modalRef = modalService.open(EditSynonymComponent, target.data.adjustModalOptions());
    modalRef.componentInstance.setColumnsDictionaries(this.columnsDictionaries);
    modalRef.componentInstance.setUUID(idParts[1]);
    modalRef.componentInstance.setIndex(idParts[2]);
    modalRef.componentInstance.notifyClose.subscribe(response => target.data.callbackOfEditSynonymClosed(response));
  }
  // callback of edit synonym closed
  private callbackOfEditSynonymClosed(repsonse: string): void {

  }
  // adjust modal options
  // if don't adjust modal options, modal will not be shown correctly
  adjustModalOptions(): NgbModalOptions {
    const options: NgbModalOptions = new NgbModalConfig();
    options.backdrop = false;
    options.windowClass = 'modal fade in';
    options.size = 'lg';
    return options;
  }
  // delete synonym handler
  private deleteSynonymHandler(target: any): void {
    const component = target.data;
  }
  // set operate formatter to synonyms
  public operateFormatter(value, row, index): string {
    // find uuid of synonyms
    const uuid = this.findUUIDOfSynonyms(value);
    if ('' === uuid) {
      // it is impossible, but if true, return value
      return value;
    } else {
      // generate id as %method%uuid%index
      const modifySynonymId = 'modify%' + uuid + '%' + index;
      const deleteSynonymId = 'delete%' + uuid + '%' + index;
      return [
        '<div class="left">',
        '<a id=' + modifySynonymId + ' href="javascript:void()">' + value + '</a>',
        '</div>',
        '<a id=' + deleteSynonymId + ' class="remove" href="javascript:void(0)" title="Remove">',
        '<i class="fa fa-trash"></i>',
        '</a>',
        '</div>'
      ].join('');
    }
  }
  // find uuid of synonyms
  private findUUIDOfSynonyms(synonymTarget: string): string {
    this.columnsDictionaries.forEach(element => {
      element.getSynonyms().forEach(synonym => {
        if (synonym === synonymTarget) {
          return element.getUUID();
        }
      }); // end get synonyms
    }); // end columnsDictionaries
    return '';
  }
}
