import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataExcelReportSelectionComponent } from './data-excel-report-selection.component';

describe('DataExcelReportSelectionComponent', () => {
  let component: DataExcelReportSelectionComponent;
  let fixture: ComponentFixture<DataExcelReportSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataExcelReportSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataExcelReportSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
