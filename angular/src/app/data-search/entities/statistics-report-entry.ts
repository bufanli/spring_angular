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
  // get specified field's value of statistics report entry
  public getComputeValueOfSpecifiedComputeField(
    specifiedComputeField: string): number {
    let ret = 0;
    this.getComputeValues().forEach(element => {
      if (element.getComputeField() === specifiedComputeField) {
        ret = element.getComputeValue();
      } else {
        // go to next
      }
    });
    return ret;
  }
}
