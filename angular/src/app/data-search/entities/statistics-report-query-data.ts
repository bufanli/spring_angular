import { ComputeField } from './compute-field';
import { SearchParamEntry } from './search-param-entry';

export class StatisticsReportQueryData {
  // group by field
  private groupByField: string = null;
  // compute fields
  private computeFields: ComputeField[] = null;
  // query conditions
  private queryConditions: SearchParamEntry[] = null;
  public getGroupByField(): string {
    return this.groupByField;
  }
  public setGroupByField(groupByField: string): void {
    this.groupByField = groupByField;
  }
  public setComputeFields(computeFields: ComputeField[]) {
    this.computeFields = computeFields;
  }
  public getComputeFields(): ComputeField[] {
    return this.computeFields;
  }
  public getQueryConditions(): SearchParamEntry[] {
    return this.queryConditions;
  }
  public setQueryConditions(queryConditions: SearchParamEntry[]): void {
    this.queryConditions = queryConditions;
  }
}
