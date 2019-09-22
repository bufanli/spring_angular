import { TestBed, inject } from '@angular/core/testing';

import { DataHeaderDisplayService } from './data-header-display.service';

describe('DataHeaderDisplayService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DataHeaderDisplayService]
    });
  });

  it('should be created', inject([DataHeaderDisplayService], (service: DataHeaderDisplayService) => {
    expect(service).toBeTruthy();
  }));
});
