import { TestBed, inject } from '@angular/core/testing';

import { MainGuardService } from './main-guard.service';

describe('MainGuardService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MainGuardService]
    });
  });

  it('should be created', inject([MainGuardService], (service: MainGuardService) => {
    expect(service).toBeTruthy();
  }));
});
