export class ComputeField {
  // compute field name
  private fieldName: string = null;
  // compute field type
  private computeType: string = null;
  public getFieldName(): string {
    return this.fieldName;
  }
  public setFieldName(fieldName: string): void {
    this.fieldName = fieldName;
  }
  public getComputeType(): string {
    return this.computeType;
  }
  public setComputeType(computeType: string) {
    this.computeType = computeType;
  }
  public constructor() {
  }
}
