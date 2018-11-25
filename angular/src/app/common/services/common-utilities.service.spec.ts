import { TestBed, inject } from '@angular/core/testing';

import { CommonUtilitiesService } from './common-utilities.service';

describe('CommonUtilitiesService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CommonUtilitiesService]
    });
  });

  it('should be created', inject([CommonUtilitiesService], (service: CommonUtilitiesService) => {
    expect(service).toBeTruthy();
  }));
});
