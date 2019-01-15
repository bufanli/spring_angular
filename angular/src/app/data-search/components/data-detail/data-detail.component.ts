import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-data-detail',
  templateUrl: './data-detail.component.html',
  styleUrls: ['./data-detail.component.css']
})
export class DataDetailComponent implements OnInit {

  // data detail fields
  public readonly DATA_DETAIL_FIELDS = [
    '日期',
    '进口',
    '进口关区',
    '主管关区',
    '装货港',
    '中转国',
    '原产国',
    '商品编码',
    '商品编码2',
    '产品名称',
    '制作或保存方法',
    '加工方法',
    '牛肉部位',
    '包装规格',
    '英文品名',
    '品牌',
    '加工厂号',
    '牛种',
    '牛龄',
    '级别',
    '饲养方式',
    '签约日期',
    '申报要素',
    '成交方式',
    '申报单价',
    '申报总价',
    '申报币制',
    '美元单价',
    '美元总价',
    '美元币制',
    '申报数量',
    '申报数量单位',
    '法定重量',
    '法定单位',
    '毛重',
    '净重',
    '重量单位',
    '贸易方式',
    '运输方式',
    '目的地',
    '包装种类',
    '申报单位',
    '货主单位',
    '经营单位',
    '企业代码',
    '企业性质',
    '地址',
    '电话',
    '传真',
    '邮编',
    'Email',
    '联系人',
    'GTIN',
    'CAS',
    '备注',
  ];
  public currentData: any = null;
  @Output() notifyClose: EventEmitter<string> = new EventEmitter<string>();
  constructor(private activeModal: NgbActiveModal) { }
  // set current data
  public setCurrentData(data: any): void {
    this.currentData = data;
  }

  ngOnInit() {
  }
  // close modal
  public close(): void {
    this.notifyClose.emit('closed');
    this.activeModal.close();
  }

}
