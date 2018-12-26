import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { UserAddBarcodeComponent } from './user-add-bar-code.component';


describe('UserAddBarcodeComponent', () => {
  let component: UserAddBarcodeComponent;
  let fixture: ComponentFixture<UserAddBarcodeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserAddBarcodeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAddBarcodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
