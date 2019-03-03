export class StatiticsFields {
  // selectable statistics types
  private statisticsTypes: string[] = null;
  // selectable group by fields
  private groupByFields: string[] = null;
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
  public getComputeFields(): string[] {
    return this.computeFields;
  }
  // setter
  public setComputeFields(computeFields: string[]) {
    this.computeFields = computeFields;
  }
}
