export class ComputeValue {
  private computeField: string;
  private computeValue: number;
  public setComputeField(computeField: string): void {
    this.computeField = computeField;
  }
  public getComputeField(): string {
    return this.computeField;
  }
  public setComputeValue(computeValue: number): void {
    this.computeValue = computeValue;
  }
  public getComputeValue(): number {
    return this.computeValue;
  }

}
