import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSynonymComponent } from './edit-synonym.component';

describe('EditSynonymComponent', () => {
  let component: EditSynonymComponent;
  let fixture: ComponentFixture<EditSynonymComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditSynonymComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditSynonymComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
