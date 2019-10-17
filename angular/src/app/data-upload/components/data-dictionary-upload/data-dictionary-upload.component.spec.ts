import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataDictionaryUploadComponent } from './data-dictionary-upload.component';

describe('DataDictionaryUploadComponent', () => {
  let component: DataDictionaryUploadComponent;
  let fixture: ComponentFixture<DataDictionaryUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataDictionaryUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataDictionaryUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
