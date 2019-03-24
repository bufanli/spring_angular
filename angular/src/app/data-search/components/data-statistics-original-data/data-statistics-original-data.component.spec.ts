import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataStatisticsOriginalDataComponent } from './data-statistics-original-data.component';

describe('DataStatisticsOriginalDataComponent', () => {
  let component: DataStatisticsOriginalDataComponent;
  let fixture: ComponentFixture<DataStatisticsOriginalDataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataStatisticsOriginalDataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataStatisticsOriginalDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
