import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ColumnsContainerService } from 'src/app/common/services/columns-container.service';

@Component({
  selector: 'app-data-detail',
  templateUrl: './data-detail.component.html',
  styleUrls: ['./data-detail.component.css']
})
export class DataDetailComponent implements OnInit {

  // data detail fields
  public readonly DATA_DETAIL_FIELDS = [
    '日期',
    '进出口',
    '申报单位名称',
    '货主单位名称',
    '经营单位名称',
    '经营单位代码',
    '运输工具名称',
    '提运单号',
    '海关编码',
    '附加码',
    '商品名称',
    '部位',
    '包装规格',
    '英文品名',
    '品牌',
    '加工厂号',
    '加工企业名称',
    '牛种',
    '牛龄',
    '级别',
    '饲养方式',
    '申报要素',
    '成交方式',
    '申报单价',
    '申报总价',
    '申报币制',
    '美元单价',
    '美元总价',
    '美元币制',
    '统计人民币价',
    '申报数量',
    '申报数量单位',
    '法定重量',
    '法定单位',
    '毛重',
    '净重',
    '重量单位',
    '件数',
    '监管方式',
    '运输方式',
    '目的地',
    '包装种类',
    '主管关区',
    '报关口岸',
    '装货港',
    '中转国',
    '贸易国',
    '企业性质',
    '运费／率',
    '运费币制',
    '保险费／率',
    '保险费币制',
    '杂费／率',
    '杂费币制',
    '地址',
    '手机',
    '电话',
    '传真',
    '邮编',
    'Email',
    '法人',
    '联系人',
  ];
  public currentData: any = null;
  private readonly NUMBER_PER_ROW = 6;
  private readonly MAX_SHOWING_LENGTH = 20;
  // detail fields by row
  public dataDetailFieldsByRow: any[] = [];
  @Output() notifyClose: EventEmitter<string> = new EventEmitter<string>();
  constructor(
    private activeModal: NgbActiveModal,
    private columnsContainerService: ColumnsContainerService) { }
  // set current data
  public setCurrentData(data: any): void {
    this.currentData = data;
  }

  ngOnInit() {
    const allColumns = this.columnsContainerService.getAllColumns();
    let dataDetailFields: any = null;
    if (allColumns !== null) {
      dataDetailFields = allColumns;
    } else {
      dataDetailFields = this.DATA_DETAIL_FIELDS;
    }
    // rearrange data fields by row
    let startPos = 0;
    for (let index = 0; index <= dataDetailFields.length / this.NUMBER_PER_ROW;) {
      let endPos = 0;
      if (startPos + this.NUMBER_PER_ROW > dataDetailFields.length) {
        endPos = dataDetailFields.length;
      } else {
        endPos = startPos + this.NUMBER_PER_ROW;
      }
      // slice array into one row
      const dataDetailFieldsOneRow = dataDetailFields.slice(startPos, endPos);
      this.dataDetailFieldsByRow.push(dataDetailFieldsOneRow);
      // increse start pos
      startPos += this.NUMBER_PER_ROW;
      index = index + 1;
    }
    // adjust modal size
    // this.adjustModalSize();
  }
  // adjust modal size
  private adjustModalSize(): void {
    $('.data-detail-container').height($(window).height() * 0.9);
    // tslint:disable-next-line: deprecation
    $('.data-detail-container').resize(function () {
      $('.data-detail-container').height($(window).height() * 0.9);
    });
  }
  // close modal
  public close(): void {
    this.notifyClose.emit('closed');
    this.activeModal.close();
  }
  // get tooltip
  public getTooltip(value: string): string {
    if (value.length > this.MAX_SHOWING_LENGTH) {
      return '<span class = "text-primary" data-toggle = "tooltip" \
                  title = ' + '"' + value + '"' + '>' + value.substring(0, this.MAX_SHOWING_LENGTH) + '..' + '</span>';
    } else {
      return value;
    }
  }

}
