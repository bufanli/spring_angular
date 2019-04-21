import { QueryCondition } from './query-condition';

export class QueryConditionRow {
  private queryConditions: QueryCondition[] = [];
  // push query condition into query conditions of row
  public pushQueryCondition(queryCondition: QueryCondition): void {
    this.queryConditions.push(queryCondition);
  }
  // get query conditions
  public getQueryConditions(): QueryCondition[] {
    return this.queryConditions;
  }
}
