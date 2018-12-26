import { TestBed, inject } from '@angular/core/testing';

import { CurrentUserContainerService } from './current-user-container.service';

describe('CurrentUserContainerService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CurrentUserContainerService]
    });
  });

  it('should be created', inject([CurrentUserContainerService], (service: CurrentUserContainerService) => {
    expect(service).toBeTruthy();
  }));
});
