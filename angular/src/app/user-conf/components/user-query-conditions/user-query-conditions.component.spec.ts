import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserQueryConditionsComponent } from './user-query-conditions.component';

describe('UserQueryConditionsComponent', () => {
  let component: UserQueryConditionsComponent;
  let fixture: ComponentFixture<UserQueryConditionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserQueryConditionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserQueryConditionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
