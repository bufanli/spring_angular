import { Component, OnInit, AfterViewChecked } from '@angular/core';

@Component({
  selector: 'app-edit-dictionary',
  templateUrl: './edit-dictionary.component.html',
  styleUrls: ['./edit-dictionary.component.css']
})
export class EditDictionaryComponent implements OnInit, AfterViewChecked {
  public originalColumns: string[] = ['日期'];
  public synonyms: any = [{
    originalColumn: '年月日',
  }];
  constructor() { }

  ngOnInit() {
  }
  ngAfterViewChecked() {
    $('#table').bootstrapTable({
      data: this.synonyms,
      height: $(window).height() * 0.6,
    });
  }

}
