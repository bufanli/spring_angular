import { ComputeValue } from './compute-value';

export class StatisticsReportEntry {
  private groupByField: string;
  private computeValues: ComputeValue[];
  public setGroupByField(groupByField: string): void {
    this.groupByField = groupByField;
  }
  public getGroupByField(): string {
    return this.groupByField;
  }
  public setComputeValues(computeValues: ComputeValue[]): void {
    this.computeValues = computeValues;
  }
  public getComputeValues(): ComputeValue[] {
    return this.computeValues;
  }
}
