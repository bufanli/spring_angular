import { UUID } from 'angular2-uuid';

export class ColumnsDictionary {
  private columnName: string = null;
  private synonyms: string[] = null;
  private uuid: string = null;
  constructor(columnName: string,
    synonyms: string[]) {
    this.synonyms = synonyms;
    this.columnName = columnName;
    this.uuid = UUID.UUID();
  }
  // get synonyms
  public getSynonyms(): string[] {
    return this.synonyms;
  }
  // get column
  public getColumnName(): string {
    return this.columnName;
  }
  // get uuid
  public getUUID(): string {
    return this.uuid;
  }
}
