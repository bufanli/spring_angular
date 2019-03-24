export class ComputeValue {
  private computeField: string;
  private computeValue: number;
  public setComputeField(computeField: string): void {
    this.computeField = computeField;
  }
  public getComputeField(): string {
    return this.computeField;
  }
  public setComputeValue(computeValue): void {
    this.computeValue = parseFloat(computeValue);
  }
  public getComputeValue(): number {
    return this.computeValue;
  }

}
