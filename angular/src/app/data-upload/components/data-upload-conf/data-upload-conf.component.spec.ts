import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataUploadConfComponent } from './data-upload-conf.component';

describe('DataUploadConfComponent', () => {
  let component: DataUploadConfComponent;
  let fixture: ComponentFixture<DataUploadConfComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataUploadConfComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataUploadConfComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
