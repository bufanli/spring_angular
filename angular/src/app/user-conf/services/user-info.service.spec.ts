import { TestBed, inject } from '@angular/core/testing';

import { UserInfoService } from './user-info.service';

describe('UserInfoServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserInfoService]
    });
  });

  it('should be created', inject([UserInfoService], (service: UserInfoService) => {
    expect(service).toBeTruthy();
  }));
});
