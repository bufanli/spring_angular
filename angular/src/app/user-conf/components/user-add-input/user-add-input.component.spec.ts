import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAddInputComponent } from './user-add-input.component';

describe('UserAddInputComponent', () => {
  let component: UserAddInputComponent;
  let fixture: ComponentFixture<UserAddInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserAddInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAddInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
