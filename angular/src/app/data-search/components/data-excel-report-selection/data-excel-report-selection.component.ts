import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Header } from 'src/app/common/entities/header';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { QueryCondition } from '../../entities/query-condition';
import { QueryConditionRow } from '../../entities/query-conditions-row';
import 'select2';
import { OptionData } from 'select2';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { CommonDialogCallback } from 'src/app/common/interfaces/common-dialog-callback';
import { ExcelReportSettingData } from '../../entities/excel-report-setting-data';

@Component({
  selector: 'app-data-excel-report-selection',
  templateUrl: './data-excel-report-selection.component.html',
  styleUrls: ['./data-excel-report-selection.component.css']
})
export class DataExcelReportSelectionComponent implements OnInit, AfterViewInit, CommonDialogCallback {

  public readonly SELECT_STATISTICS_REPORT_TYPE = '请选择汇总设定';
  public readonly PLEASE_SELECT = '请选择';
  public readonly CLOSE_BUTTON = '关闭';
  public readonly EXPORT_EXCEL_REPORT = '导出汇总报告';
  public readonly EXCEL_REPORT_TYPES_TITLE = '导出汇总报告类型';
  public readonly NO_EXCEL_REPORT_TYPES_SELECTED = '请至少选择一个汇总报告类型';
  public readonly EXPORT_EXCEL_SEETING_ERROR_TITLE = '请检查汇总报告设定';
  public readonly EXCEL_REPORT_TYPES_FIELD = 'excel-report-type';
  public readonly SELECTED = 'selected';
  public readonly QUERY_CONDITIONS_NUMBER_EACH_ROW = 3;
  public readonly LIST_VALUE_QUERY_NUMBER_PER_PAGE = 100;
  private readonly GET_LIST_VALUE_URL = 'api/getListValueWithPagination';  // URL to web api
  private readonly NO_EXCEL_REPORT_TYPE_SOURCE_ID = '001';
  // excel report query conditions
  public queryConditions: QueryCondition[] = null;
  public queryConditionRows: any[] = null;
  // excel report types
  public excelReportTypes: any[] = null;
  public excelReportTypesTableValues: any[] = null;
  // data statistics service
  private excelReportService: any = null;
  // check parameter error msg
  private checkParametersErrorMsg: string = null;
  // error exist
  public errorExist = false;
  // error message
  public errorMsg: string = null;
  constructor(private activeModal: NgbActiveModal,
    private commonUtilitiesService: CommonUtilitiesService) { }

  ngOnInit() {
  }
  ngAfterViewInit() {
    // init excel report types table
    this.initExcelReportTypes();
    // init selector
    this.setSelectOptions();
    // load data into excel report types table
    this.loadExcelReportTypesIntoTable();

  }
  // public set data statistics service
  public setExcelReportService(excelReportService: any): void {
    this.excelReportService = excelReportService;
  }
  // close dialogue
  public close(): void {
    this.activeModal.close();
  }
  // set query conditions
  public setExcelReportQueryConditions(queryConditions: QueryCondition[]) {
    this.queryConditions = queryConditions;
    // reshape query conditions to rows
    this.reshapeQueryConditionsIntoRows();
  }
  // convert excel report types from string arrry to {}
  public setExcelReportTypes(tempExcelReportTypes: any): void {
    this.excelReportTypes = [];
    tempExcelReportTypes.forEach(element => {
      // set every excel report type to true in default
      const tempReportType = {};
      tempReportType[this.EXCEL_REPORT_TYPES_FIELD] = element;
      tempReportType[this.SELECTED] = true;
      this.excelReportTypes.push(tempReportType);
    });
    // convert excel report types to table data
    this.convertExcelReportTypesToTableData();
  }
  // init excel report types table
  private initExcelReportTypes(): void {
    // if donot destroy table, it would not show
    $('#excel-report-types').bootstrapTable('destroy');
    // set headers into table
    const excelReportTypesHeaders: any[] = [];
    // checkbox to set display on excel report types or not
    const that = this;
    const checkboxHeader = {
      align: 'center',
      checkbox: 'true',
      formatter: function (index, row) {
        const ret = {};
        // set checked state
        ret['checked'] = row.value;
        return ret;
      }// end of formatter
    };
    excelReportTypesHeaders.push(checkboxHeader);
    // query conditions display header
    const excelReportTypeHeader = new Header(
      this.EXCEL_REPORT_TYPES_FIELD,
      this.EXCEL_REPORT_TYPES_TITLE,
      true,
      'center');
    excelReportTypesHeaders.push(excelReportTypeHeader);
    // load header of table
    $('#excel-report-types').bootstrapTable({
      columns: excelReportTypesHeaders,
      pagination: false,
      height: $(window).height() * 0.5,
      onCheck: function (row: any, $element: any): void {
        const excelReportType = row[that.EXCEL_REPORT_TYPES_FIELD];
        that.excelReportTypes.forEach(element => {
          if (element[that.EXCEL_REPORT_TYPES_FIELD] === excelReportType) {
            element[that.SELECTED] = true;
          }
        });
      },
      onCheckAll: function (rows: any[]): void {
        rows.forEach(row => {
          const excelReportType = row[that.EXCEL_REPORT_TYPES_FIELD];
          that.excelReportTypes.forEach(element => {
            if (element[that.EXCEL_REPORT_TYPES_FIELD] === excelReportType) {
              element[that.SELECTED] = true;
            }
          });
        });
      },
      onUncheck: function (row: any, $element: any): void {
        const excelReportType = row[that.EXCEL_REPORT_TYPES_FIELD];
        that.excelReportTypes.forEach(element => {
          if (element[that.EXCEL_REPORT_TYPES_FIELD] === excelReportType) {
            element[that.SELECTED] = false;
          }
        });
      },
      onUncheckAll: function (rows: any[]): void {
        rows.forEach(row => {
          const excelReportType = row[that.EXCEL_REPORT_TYPES_FIELD];
          that.excelReportTypes.forEach(element => {
            if (element[that.EXCEL_REPORT_TYPES_FIELD] === excelReportType) {
              element[that.SELECTED] = false;
            }
          });
        });
      }
    });
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#excel-report-types').bootstrapTable('resetView', {
        height: $(window).height() * 0.5,
      });
    });
  }
  // convert excel report types to table data
  private convertExcelReportTypesToTableData(): void {
    this.excelReportTypesTableValues = [];
    this.excelReportTypes.forEach(element => {
      const entry = {};
      entry[this.EXCEL_REPORT_TYPES_FIELD] = element[this.EXCEL_REPORT_TYPES_FIELD];
      entry['value'] = element[this.SELECTED];
      this.excelReportTypesTableValues.push(entry);
    });
  }
  // load excel types data into table
  private loadExcelReportTypesIntoTable(): void {
    const that = this;
    $('#excel-report-types').bootstrapTable('load', that.excelReportTypesTableValues);
  }
  // init select picker
  private setSelectOptions(): void {
    this.queryConditions.forEach(element => {
      this.setEachSelectOptions(element);
    });
  }
  // reshape query condition into row
  private reshapeQueryConditionsIntoRows(): void {
    // rows amount
    let rowsNumber = Math.floor(this.queryConditions.length / this.QUERY_CONDITIONS_NUMBER_EACH_ROW);
    const extra = this.queryConditions.length % this.QUERY_CONDITIONS_NUMBER_EACH_ROW;
    if (extra > 0) {
      rowsNumber = rowsNumber + 1;
    }
    // prepare query condition row
    this.queryConditionRows = [];
    for (let i = 0; i < rowsNumber; i++) {
      this.queryConditionRows.push(new QueryConditionRow());
    }
    // put every query condition into ervery fix row
    for (let i = 0; i < this.queryConditions.length; i++) {
      const row = Math.floor(i / this.QUERY_CONDITIONS_NUMBER_EACH_ROW);
      this.queryConditionRows[row].pushQueryCondition(this.queryConditions[i]);
    }
  }
  // set select options to select2
  private setEachSelectOptions(listQueryCondition: QueryCondition): void {
    const that = this;
    $('#' + listQueryCondition.getUUID()).select2({
      placeholder: this.PLEASE_SELECT + listQueryCondition.getKey(),
      multiple: false,
      width: 'resolve',
      closeOnSelect: true,
      ajax: {
        url: this.GET_LIST_VALUE_URL,
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        delay: 500, // wait 500 milliseconds before triggering the request
        data: function (params): any {
          const currentPage = params.page || 1;
          const query = {
            queryCondition: listQueryCondition.getKey(),
            term: params.term || '',
            offset: (currentPage - 1) * that.LIST_VALUE_QUERY_NUMBER_PER_PAGE,
            limit: that.LIST_VALUE_QUERY_NUMBER_PER_PAGE,
          };
          return JSON.stringify(query);
        },
        processResults: function (data, params) {
          params.page = params.page || 1;
          return {
            results: data.data.results,
            pagination: {
              more: ((params.page * that.LIST_VALUE_QUERY_NUMBER_PER_PAGE) < data.data.totalCount),
            },
          };
        },
      }
    });
    $('#' + listQueryCondition.getUUID()).on('select2:open', function (e) {
      that.selectionChanged(e);
    });
  }
  // check excel report parameters
  private checkExcleReportParameters(): boolean {
    // clear error msg
    this.checkParametersErrorMsg = '';
    // check query conditions
    this.queryConditions.forEach(element => {
      // get selected options
      const selections: OptionData[] = $('#' + element.getUUID()).select2('data');
      if (selections.length === 0) {
        this.checkParametersErrorMsg =
          this.checkParametersErrorMsg +
          this.PLEASE_SELECT +
          element.getKey() + ',';
      }
    });
    // check excel report types
    let checkedCount = 0;
    this.excelReportTypes.forEach(element => {
      if (element[this.SELECTED] === true) {
        checkedCount++;
      }
    });
    if (checkedCount === 0) {
      this.checkParametersErrorMsg =
        this.checkParametersErrorMsg +
        this.NO_EXCEL_REPORT_TYPES_SELECTED + ',';
    }
    if (this.checkParametersErrorMsg !== '') {
      if (this.checkParametersErrorMsg.endsWith(',')) {
        this.checkParametersErrorMsg = this.checkParametersErrorMsg.substring
          (0,
            this.checkParametersErrorMsg.length - ','.length
          );
      }
      return false;
    } else {
      return true;
    }
  }
  // excel report
  public excelReport(): void {
    // check excel report parameters
    if (this.checkExcleReportParameters() === false) {
      this.errorExist = true;
      this.errorMsg = this.checkParametersErrorMsg;
      return;
    } else {
      // prepare excel report data -- query conditions
      const queryConditionsRet: QueryCondition[] = [];
      this.queryConditions.forEach(element => {
        this.abstractInputQueryConditionWithListType(queryConditionsRet, element);
      });
      // prepare excel report data -- excel report types
      const excelReportTypes: string[] = [];
      this.excelReportTypes.forEach(element => {
        if (element[this.SELECTED] === true) {
          excelReportTypes.push(element[this.EXCEL_REPORT_TYPES_FIELD]);
        }
      });
      // excel report setting data
      const excelReportSettingData: ExcelReportSettingData = new ExcelReportSettingData();
      excelReportSettingData.setQueryConditions(queryConditionsRet);
      excelReportSettingData.setExcxelReportTypes(excelReportTypes);
      this.excelReportService.excelReport(excelReportSettingData);
    }
  }
  // abstract input query condition of list type
  private abstractInputQueryConditionWithListType(
    queryParams: QueryCondition[],
    queryCondition: QueryCondition): void {
    // if nothing is selected, then do not put into query params
    const selections: OptionData[] = $('#' + queryCondition.getUUID()).select2('data');
    const selectionsArray = [];
    selections.forEach(element => {
      selectionsArray.push(element.text);
    });
    // convert selection from comma to dash
    const selectionsStr = this.commonUtilitiesService.convertArrayCommaSeperatorToDash(selectionsArray);
    const inputQueryCondition = queryCondition.clone();
    inputQueryCondition.setStringValue(selectionsStr);
    queryParams.push(inputQueryCondition);
  }
  // common dialog callback
  callbackOnConfirm(sourceID: any): void {
    if (sourceID === this.NO_EXCEL_REPORT_TYPE_SOURCE_ID) {
      // nothing to do
    }
  }
  // selection changed
  public selectionChanged(param: any): void {
    this.clearErrorMsg();
  }
  // clear error message
  private clearErrorMsg(): void {
    this.errorExist = false;
    this.errorMsg = null;
  }
}
