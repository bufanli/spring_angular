export class QueryCondition {
  // key of query condition
  private key: string = null;
  // value of query condition
  private value: any = null;
  // type of query condition
  private type: string = null;
  // uuid
  private uuid: string = null;
  // constructor
  public constructor(key: string, value: string, type: string) {
    this.key = key;
    this.type = type;
    this.setValue(value);
  }
  // get value
  public getValue(): string[] {
    return this.value;
  }
  // set value
  public setValue(value: String): void {
    this.value = [];
    if (value === null) {
      return;
    }
    const valuesTemp = value.split('~~');
    valuesTemp.forEach(element => {
      if (this.type === 'List') {
        if (element !== null && element !== '') {
          this.value.push(element);
        }
      } else {
        this.value.push(element);
      }
    });
  }
  // set string value
  public setStringValue(value: String): void {
    this.value = value;
  }
  // get type
  public getType(): string {
    return this.type;
  }
  // set type
  public setType(type: string): void {
    this.type = type;
  }
  // get key
  public getKey(): string {
    return this.key;
  }
  // set key
  public setKey(key: string): void {
    this.key = key;
  }
  // set uuid
  public setUUID(uuid: string): void {
    this.uuid = uuid;
  }
  // get uuid
  public getUUID(): string {
    return this.uuid;
  }
  // clone query condition to another query condition
  // but not includes uuid for sending reuqest to spring
  public clone(): QueryCondition {
    const cloned = new QueryCondition(this.key, '', this.type);
    return cloned;
  }
}
