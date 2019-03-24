import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataStatisticsGraphComponent } from './data-statistics-graph.component';

describe('DataStatisticsGraphComponent', () => {
  let component: DataStatisticsGraphComponent;
  let fixture: ComponentFixture<DataStatisticsGraphComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataStatisticsGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataStatisticsGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
