import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCustomColumnComponent } from './add-custom-column.component';

describe('AddCustomColumnComponent', () => {
  let component: AddCustomColumnComponent;
  let fixture: ComponentFixture<AddCustomColumnComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddCustomColumnComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCustomColumnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
