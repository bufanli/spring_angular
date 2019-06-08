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
  // clone
  public clone(): ColumnsDictionary {
    // cloned synonyms
    const clonedSynonyms: string[] = [];
    this.synonyms.forEach(element => {
      clonedSynonyms.push(element);
    });
    const cloned = new ColumnsDictionary(
      this.columnName,
      clonedSynonyms,
    );
    cloned.uuid = null;
    return cloned;
  }
  // remove specified synonym
  public removeSpecifiedSynonym(specifiedSynonym: string): void {
    let index = -1;
    for (let i = 0; i < this.synonyms.length; i++) {
      if (this.synonyms[i] === specifiedSynonym) {
        index = i;
        break;
      }
    }
    if (index !== -1) {
      // found
      this.synonyms.splice(index, 1);
    } else {
      // not found, nothing to do
    }
  }
}
