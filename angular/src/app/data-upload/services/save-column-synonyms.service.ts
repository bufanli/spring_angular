import { Injectable } from '@angular/core';
import { ColumnsDictionary } from '../entities/columns-dictionary';
import { SaveColumnDictionaryCallback } from '../interfaces/save-column-dictionary-callback';
import { Observable } from 'rxjs';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { HttpClient, HttpHeaders } from '@angular/common/http';

// json header for post
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class SaveColumnSynonymsService {

  private readonly saveColumnDictionariesURL = 'api/setColumnsDictionary';
  // column dictionary
  private columnDictionaries: ColumnsDictionary[] = null;
  // save end callback
  private saveEndCallback: SaveColumnDictionaryCallback = null;
  private readonly DELETE_COLUMN_NAME = 'deletecolumn_';
  private readonly ADD_COLUMN_HEADER = '添加自定义列';
  constructor(private http: HttpClient) { }
  // set columnDictionaries
  public setColumnDictionaries(columnDictionaries: ColumnsDictionary[]): void {
    this.columnDictionaries = columnDictionaries;
  }
  // set callback of save column synonym end
  public setCallback(saveColumnSynonymCallback: SaveColumnDictionaryCallback): void {
    this.saveEndCallback = saveColumnSynonymCallback;
  }
  // save synonym dictionaries
  public saveColumnDictionaries(): void {
    this.saveColumnDictionariesImpl().subscribe
      (httpResponse => this.saveColumnDictionariesNotification(httpResponse));
  }
  // save synonym dictionaries implementation
  private saveColumnDictionariesImpl(): Observable<HttpResponse> {
    return this.http.post<HttpResponse>(
      this.saveColumnDictionariesURL,
      this.purifyColumnDictionaries(),
      httpOptions);
  }
  // purify column dictionaries
  private purifyColumnDictionaries(): ColumnsDictionary[] {
    const purifiedColumnDictionaries: ColumnsDictionary[] = [];
    this.columnDictionaries.forEach(element => {
      // if it is just for adding custom column
      // nothing to do
      if (element.getColumnName() === this.ADD_COLUMN_HEADER) {
        // nothing to do
      } else {
        const clonedColumnDictionary = element.clone();
        // remove specified synonym
        clonedColumnDictionary.removeSpecifiedStartSynonym(this.DELETE_COLUMN_NAME);
        purifiedColumnDictionaries.push(clonedColumnDictionary);
      }
    });
    return purifiedColumnDictionaries;
  }
  // save column dictionary notification
  private saveColumnDictionariesNotification(httpResponse: HttpResponse): void {
    this.saveEndCallback.callbackOnSaveEnd(httpResponse);
  }
}
