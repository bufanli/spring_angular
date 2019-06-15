import { TestBed, inject } from '@angular/core/testing';

import { ColumnsContainerService } from './columns-container.service';

describe('ColumnsContainerService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ColumnsContainerService]
    });
  });

  it('should be created', inject([ColumnsContainerService], (service: ColumnsContainerService) => {
    expect(service).toBeTruthy();
  }));
});
