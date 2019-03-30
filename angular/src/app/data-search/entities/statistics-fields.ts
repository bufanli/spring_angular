export class StatisticsFields {
  // selectable statistics types
  private statisticsTypes: string[] = null;
  // selectable group by fields
  private groupByFields: string[] = null;
  // selectable sub group by fields
  private groupBySubFields: string[] = null;
  // selectable compute fields
  private computeFields: string[] = null;
  // getter
  public getStatisticsTypes(): string[] {
    return this.statisticsTypes;
  }
  // setter
  public setStatisticsTypes(statisticsTypes: string[]) {
    this.statisticsTypes = statisticsTypes;
  }
  // getter
  public getGroupByFields(): string[] {
    return this.groupByFields;
  }
  public setGroupByFields(groupByFields: string[]) {
    this.groupByFields = groupByFields;
  }
  // getter
  public getGroupBySubFields(): string[] {
    return this.groupBySubFields;
  }
  public setGroupBySubFields(groupBySubFields: string[]) {
    this.groupBySubFields = groupBySubFields;
  }
  // getter
  public getComputeFields(): string[] {
    return this.computeFields;
  }
  // setter
  public setComputeFields(computeFields: string[]) {
    this.computeFields = computeFields;
  }
}
