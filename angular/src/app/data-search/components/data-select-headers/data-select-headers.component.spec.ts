import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataSelectHeadersComponent } from './data-select-headers.component';

describe('DataSelectHeadersComponent', () => {
  let component: DataSelectHeadersComponent;
  let fixture: ComponentFixture<DataSelectHeadersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataSelectHeadersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataSelectHeadersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
