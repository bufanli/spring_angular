import { QueryCondition } from './query-condition';

export class ExcelReportSettingData {
  // query conditions
  private queryConditions: QueryCondition[] = null;
  // excel report types
  private excelReportTypes: string[] = null;
  // set/get/query conditions
  public setQueryConditions(queryConditions: QueryCondition[]): void {
    this.queryConditions = queryConditions;
  }
  public getQueryConditions(): QueryCondition[] {
    return this.queryConditions;
  }
  // set/get excel report types
  public setExcxelReportTypes(excelReportTypes: string[]): void {
    this.excelReportTypes = excelReportTypes;
  }
  public getExcelReportTypes(): string[] {
    return this.excelReportTypes;
  }
}
