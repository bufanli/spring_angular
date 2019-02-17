import { Component, OnInit, AfterContentInit, AfterViewChecked, AfterViewInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
declare const require: any;

@Component({
  selector: 'app-data-statistics',
  templateUrl: './data-statistics.component.html',
  styleUrls: ['./data-statistics.component.css']
})
export class DataStatisticsComponent implements OnInit, AfterViewInit {

  options: any;
  // selected statistics type
  type: string = null;
  // statistics type list
  public readonly STATISTICS_TYPES: string[] = ['柱状图', '饼状图', '折线图'];

  constructor(private activeModal: NgbActiveModal) { }
  // get statistics type
  public getStatisticsTypes(): string[] {
    return this.STATISTICS_TYPES;
  }

  ngOnInit() {
    const xAxisData = [];
    const data1 = [];
    const data2 = [];

    for (let i = 0; i < 100; i++) {
      xAxisData.push('category' + i);
      data1.push((Math.sin(i / 5) * (i / 5 - 10) + i / 6) * 5);
      data2.push((Math.cos(i / 5) * (i / 5 - 10) + i / 6) * 5);
    }

    this.options = {
      legend: {
        data: ['bar', 'bar2'],
        align: 'left'
      },
      tooltip: {
        feature: {
          saveAsImage: {},
        }
      },
      xAxis: {
        data: xAxisData,
        silent: false,
        splitLine: {
          show: false
        }
      },
      yAxis: {
      },
      series: [{
        name: 'bar',
        type: 'bar',
        data: data1,
        animationDelay: function (idx) {
          return idx * 10;
        }
      }, {
        name: 'bar2',
        type: 'bar',
        data: data2,
        animationDelay: function (idx) {
          return idx * 10 + 100;
        }
      }],
      animationEasing: 'elasticOut',
      animationDelayUpdate: function (idx) {
        return idx * 5;
      }
    };
  }
  public close(): void {
    this.activeModal.close();
  }
  // just for select picker
  ngAfterViewInit(): void {
    // if call selectpicker in ngOnInit, select control will not be shown for some reason
    // but call selectpicker can resolve this issue in ngAfterViewInit
    this.setSelectOptions('#statistics-type', false);
  }

  // init select picker
  private setSelectOptions(id: string, liveSearch: boolean): void {
    $(id).selectpicker({
      'liveSearch': liveSearch,
    });
    $(id).selectpicker('val', '');
    $(id).selectpicker('refresh');
  }
}
