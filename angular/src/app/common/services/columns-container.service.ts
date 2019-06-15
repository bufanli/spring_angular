import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpResponse } from '../entities/http-response';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ColumnsContainerService {

  private readonly getColumnDictionaryURL = 'api/getColumnsDictionary';
  // all columns
  private allColumns: string[] = null;
  constructor(private http: HttpClient) { }
  // get all columns
  public getAllColumns(): string[] {
    return this.allColumns;
  }
  // initial function
  public init(): void {
    // if allColumns is null, then load all columns,
    // otherwise donot load
    if (this.allColumns === null) {
      this.loadAllColumns();
    }
  }
  // refresh all columns
  public refreshAllColumns(): void {
    this.loadAllColumns();
  }
  // get all columns at initial time
  private loadAllColumns(): void {
    // get columns and synonyms
    this.getColumnsDictionaryImpl().subscribe(httpResponse =>
      this.getColumnsDictionaryNotification(httpResponse));
  }
  // get columns from server implementation
  private getColumnsDictionaryImpl(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getColumnDictionaryURL);
  }
  private getColumnsDictionaryNotification(httpResponse: HttpResponse): void {
    if (httpResponse.code === 200) {
      // get columns dictionary ok
      const columnsDictionaries: any = httpResponse.data;
      // convert column dictionary entries to columns
      this.allColumns = this.convertColumnDictionariesToColumns(httpResponse.data);
    }
  }
  // convert http response to columns
  // convert column dictionary entries to columns array
  private convertColumnDictionariesToColumns(columnsData: any[]): string[] {
    const columns = [];
    if (columns === null) {
      return null;
    }
    columnsData.forEach(element => {
      columns.push(element.columnName);
    });
    return columns;
  }
}
