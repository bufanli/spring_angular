import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAccessAuthoritiesComponent} from './user-access-authorities.component';

describe('UserAccessAuthoritiesComponent', () => {
  let component: UserAccessAuthoritiesComponent;
  let fixture: ComponentFixture<UserAccessAuthoritiesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserAccessAuthoritiesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAccessAuthoritiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
