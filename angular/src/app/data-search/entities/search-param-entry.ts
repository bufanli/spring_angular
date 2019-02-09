export class SearchParamEntry {
  private key: string = null;
  private value: string = null;
  private type: string = null;
  public constructor(key: string,
    value: string,
    type: string) {
    this.key = key;
    this.value = value;
    this.type = type;
  }
}
