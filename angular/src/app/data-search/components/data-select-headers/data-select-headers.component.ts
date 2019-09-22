import { Component, OnInit } from '@angular/core';
import { CommonUtilitiesService } from 'src/app/common/services/common-utilities.service';
import { Header } from 'src/app/common/entities/header';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SaveHeaderDisplaysCallback } from '../../entities/save-header-displays-callback';
import { HttpResponse } from 'src/app/common/entities/http-response';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';

@Component({
  selector: 'app-data-select-headers',
  templateUrl: './data-select-headers.component.html',
  styleUrls: ['./data-select-headers.component.css']
})
export class DataSelectHeadersComponent implements OnInit, SaveHeaderDisplaysCallback {

  // because query condition max number is defined in user
  // access authorities,it is necessary to get user access authorities here
  // private currentUserAccessAuthorities: UserAccessAuthorities;
  // query condition title
  public readonly HEADER_DISPLAY_TITLE = '用户数据表头设定';
  // max header display number
  public readonly MAX_HEADER_DISPLAY_NUMBER = 15;
  // disabled checkbox headers
  private readonly DISABLED_CHECKBOX_HEADER_DISPLAYS = [];
  // query condition field
  private readonly HEADER_DISPLAY_FIELD = 'data-header';
  // max header display exceeded error
  private readonly BEYOND_MAX_HEADER_DISPLAY_ERROR_TITLE = '超过最大数';
  private readonly BEYOND_MAX_HEADER_DISPLAY_ERROR_BODY = '已经超过最大数据表头数，最大数据表头数是';
  private readonly BEYOND_MAX_HEADER_DISPLAY_ERROR_TYPE = 'warning';
  // save error flag
  public isSaveError = false;
  // save error message
  public saveErrorMsg: string = null;
  // query conditions
  private headerDisplays: any = null;
  // header display service
  private headerDisplaysService: any = null;
  constructor(private commonUtilitiesService: CommonUtilitiesService,
    private activeModal: NgbActiveModal,
    private currentUserContainer: CurrentUserContainerService,
  ) { }

  ngOnInit() {
    // if donot destroy table, it would not show
    $('#get-headers-setting-table').bootstrapTable('destroy');
    // set headers into table
    const tableDisplaysHeaders: any[] = [];
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
        that.DISABLED_CHECKBOX_HEADER_DISPLAYS.forEach(element => {
          if (row[that.HEADER_DISPLAY_FIELD] === element) {
            ret['disabled'] = 'disabled';
            return ret;
          } else {
            // continue
          }
        });
        return ret;
      }// end of formatter
    };
    tableDisplaysHeaders.push(checkboxHeader);
    // query conditions display header
    const displayHeader = new Header(this.HEADER_DISPLAY_FIELD, this.HEADER_DISPLAY_TITLE);
    tableDisplaysHeaders.push(displayHeader);
    // load header of table
    $('#get-headers-setting-table').bootstrapTable({
      columns: tableDisplaysHeaders,
      pagination: false,
      height: $(window).height() * 0.5,
      onCheck: function (row: any, $element: any): void {
        // clear error
        that.clearError();
        const header = row[that.HEADER_DISPLAY_FIELD];
        // const maxNumber = that.currentUserAccessAuthorities['显示查询条件最大数'];
        const maxNumber = that.MAX_HEADER_DISPLAY_NUMBER;
        if (that.hasBeyondMaxNumber(maxNumber)) {
          $element[0].checked = false;
          that.commonUtilitiesService.showSimpleDialog(
            that.BEYOND_MAX_HEADER_DISPLAY_ERROR_TITLE,
            that.BEYOND_MAX_HEADER_DISPLAY_ERROR_BODY + maxNumber,
            that.BEYOND_MAX_HEADER_DISPLAY_ERROR_TYPE);
        } else {
          // that.queryConditionDisplays[queryCondition] = true;
          that.updateHeaderDisplay(header, true);
        }
      },
      onUncheck: function (row: any, $element: any): void {
        // clear error
        that.clearError();
        const header = row[that.HEADER_DISPLAY_FIELD];
        that.updateHeaderDisplay(header, false);
      },
    });
    // tslint:disable-next-line: deprecation
    $(window).resize(function () {
      $('#get-headers-setting-table').bootstrapTable('resetView', {
        height: $(window).height() * 0.5,
      });
    });
    // convert query condition displays to data
    const headerDisplays: any[] =
      this.convertHeaderDisplaysToTableData();
    // load data into table
    $('#get-headers-setting-table').bootstrapTable('load', headerDisplays);
  }
  // convert query conditions displays to table data
  private convertHeaderDisplaysToTableData(): any[] {
    const tableData = [];
    // iterate all query condition displays
    // tslint:disable-next-line:forin
    for (const headerDisplay of this.headerDisplays) {
      // user ID is not considered as query condition
      if (headerDisplay['key'] === 'userID' || headerDisplay['key'] === 'id') {
        continue;
      } else {
        const entry = {};
        entry[this.HEADER_DISPLAY_FIELD] = headerDisplay['key'];
        let checked = false;
        if (headerDisplay['value'] === 'TRUE') {
          checked = true;
        } else {
          checked = false;
        }
        entry['value'] = checked;
        tableData.push(entry);
      }
    }
    return tableData;
  }
  private updateHeaderDisplay(header: string, checked: boolean): void {
    // value for boolean string
    let checkedString = null;
    if (checked === true) {
      checkedString = 'TRUE';
    } else {
      checkedString = 'FALSE';
    }
    for (const headerDisplay of this.headerDisplays) {
      if (headerDisplay['key'] === header) {
        headerDisplay['value'] = checkedString;
      }
    }
  }
  // check if it is beyond max query condition number
  private hasBeyondMaxNumber(maxNumber: number): boolean {
    let currentNumber = 0;
    for (const headerDisplay of this.headerDisplays) {
      if (headerDisplay['value'] === 'TRUE') {
        currentNumber++;
      }
    }
    if (currentNumber >= maxNumber) {
      return true;
    } else {
      return false;
    }
  }
  // set user access authorities
  // public setUserAccessAuthorities(currentUserAccessAuthorities: UserAccessAuthorities): void {
  // this.currentUserAccessAuthorities = currentUserAccessAuthorities;
  // }
  // close modal
  public close(): void {
    this.activeModal.close();
  }
  // set header display
  public setHeaderDisplays(headerDisplays: any): void {
    this.headerDisplays = headerDisplays;
  }
  // save header display
  public saveHeaderDisplays(): void {
    // clear error
    this.clearError();
    // set callback of saving header displays
    this.headerDisplaysService.appendSaveEndCallback(this);
    // save header displays
    this.headerDisplaysService.saveHeaderDisplays(this.filterHeaderDisplays());
  }
  // save header display callback
  public callbackOnEndSaveHeaderDisplays(httpResponse: HttpResponse): void {
    if (httpResponse.code === 201) {
      // session timeout
      this.activeModal.close();
      this.currentUserContainer.sessionTimeout();
    } else if (httpResponse.code !== 200) {
      // save failed
      this.isSaveError = true;
      this.saveErrorMsg = httpResponse.message;
    } else {
      this.activeModal.close();
    }
  }
  // get rid of unnecessary header display, id and userID
  private filterHeaderDisplays(): any[] {
    const headerDisplaysRet = [];
    for (const headerDisplay of this.headerDisplays) {
      if (headerDisplay['key'] !== 'id' && headerDisplay['key'] !== 'userID') {
        headerDisplaysRet.push(headerDisplay);
      }
    }
    return headerDisplaysRet;
  }
  // clear error
  private clearError(): void {
    this.isSaveError = false;
    this.saveErrorMsg = null;
  }
  // set header display service
  public setHeaderDisplaysService(service: any): void {
    this.headerDisplaysService = service;
  }
}
