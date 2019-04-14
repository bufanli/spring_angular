import { Component, OnInit, Input } from '@angular/core';
import { Header } from 'src/app/common/entities/header';
import { query } from '@angular/core/src/render3/query';
import { UserAccessAuthorities } from '../../entities/user-access-authorities';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';

@Component({
  selector: 'app-user-query-conditions',
  templateUrl: './user-query-conditions.component.html',
  styleUrls: ['./user-query-conditions.component.css']
})
export class UserQueryConditionsComponent implements OnInit {

  // because query condition max number is defined in user
  // access authorities,it is necessary to get user access authorities here
  @Input() currentUserAccessAuthorities: UserAccessAuthorities;
  // query condition title
  private readonly QUERY_CONDITION_TITLE = '查询条件';
  // disabled checkbox query conditions
  private readonly DISABLED_CHECKBOX_QUERY_CONDITIONS = ['日期', '海关编码'];
  // query condition field
  private readonly QUERY_CONDITION_FIELD = 'query-condition';
  // max query conditions exceeded error
  private readonly BEYOND_MAX_QUERY_CONDITIONS_ERROR_TITLE = '超过最大查询条件数';
  private readonly BEYOND_MAX_QUERY_CONDITIONS_ERROR_BODY = '已经超过最大查询条件数，最大查询条件数是';
  private readonly BEYOND_MAX_QUERY_CONDITIONS_ERROR_TYPE = 'warning';
  // query conditions
  private queryConditionDisplays: any = null;
  constructor(private commonUtilitiesService: CommonUtilitiesService) { }

  ngOnInit() {
    // if donot destroy table, it would not show
    $('#query-condition-displays-table').bootstrapTable('destroy');
    // set headers into table
    const queryConditionDisplaysHeaders: any[] = [];
    // checkbox to set display on query conditions or not
    const that = this;
    const checkboxHeader = {
      align: 'center',
      checkbox: 'true',
      formatter: function (index, row) {
        const ret = {};
        // set checked state
        ret['checked'] = row.value;
        // set disable state
        // tslint:disable-next-line:forin
        that.DISABLED_CHECKBOX_QUERY_CONDITIONS.forEach(element => {
          if (row[that.QUERY_CONDITION_FIELD] === element) {
            ret['disabled'] = 'disabled';
            return ret;
          } else {
            // continue
          }
        });
        return ret;
      }// end of formatter
    };
    queryConditionDisplaysHeaders.push(checkboxHeader);
    // query conditions display header
    const queryCondtionDisplayHeader = new Header(this.QUERY_CONDITION_FIELD, this.QUERY_CONDITION_TITLE);
    queryConditionDisplaysHeaders.push(queryCondtionDisplayHeader);
    // load header of table
    $('#query-condition-displays-table').bootstrapTable({
      columns: queryConditionDisplaysHeaders,
      pagination: false,
      height: $(window).height() * 0.5,
      onCheck: function (row: any, $element: any): void {
        const queryCondition = row[that.QUERY_CONDITION_FIELD];
        const maxNumber = that.currentUserAccessAuthorities['显示查询条件数'];
        if (that.hasBeyondMaxNumber(maxNumber)) {
          that.commonUtilitiesService.showSimpleDialog(
            that.BEYOND_MAX_QUERY_CONDITIONS_ERROR_TITLE,
            that.BEYOND_MAX_QUERY_CONDITIONS_ERROR_BODY + maxNumber,
            that.BEYOND_MAX_QUERY_CONDITIONS_ERROR_TYPE);
          $element[0].checked = false;
        }
        that.queryConditionDisplays[queryCondition] = true;
      },
      onUncheck: function (row: any, $element: any): void {
        const queryCondition = row[that.QUERY_CONDITION_FIELD];
        that.queryConditionDisplays[queryCondition] = false;
      },
    });
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#query-condition-displays-table').bootstrapTable('resetView', {
        height: $(window).height() * 0.5,
      });
    });
    // convert query condition displays to data
    const queryConditionDisplays: any[] =
      this.convertQueryConditionDisplaysToTableData();
    // load data into table
    $('#query-condition-displays-table').bootstrapTable('load', queryConditionDisplays);
  }
  // set query conditions
  public setQueryConditionDisplays(queryConditionDisplays: any) {
    this.queryConditionDisplays = queryConditionDisplays;
  }
  // set user access authorities
  public setUserAccessAuthorities(currentUserAccessAuthorities: UserAccessAuthorities): void {
    this.currentUserAccessAuthorities = currentUserAccessAuthorities;
  }
  // convert query conditions displays to table data
  private convertQueryConditionDisplaysToTableData(): any[] {
    const tableData = [];
    // iterate all query condition displays
    // tslint:disable-next-line:forin
    for (const queryCondition in this.queryConditionDisplays) {
      // user ID is not considered as query condition
      if (queryCondition === 'userID') {
        continue;
      } else {
        const entry = {};
        entry[this.QUERY_CONDITION_FIELD] = queryCondition;
        entry['value'] = this.queryConditionDisplays[queryCondition];
        tableData.push(entry);
      }
    }
    return tableData;
  }
  // get query condition displays
  public getQueryCondtionDisplays(): any {
    return this.queryConditionDisplays;
  }
  // check if it is beyond max query condition number
  private hasBeyondMaxNumber(maxNumber: number): boolean {
    let currentNumber = 0;
    for (const queryCondition in this.queryConditionDisplays) {
      if (this.queryConditionDisplays[queryCondition] === true) {
        currentNumber++;
      }
    }
    if (currentNumber >= maxNumber) {
      return true;
    } else {
      return false;
    }
  }
}
