import { TestBed, inject } from '@angular/core/testing';

import { DataSearchConstListService } from './data-search-const-list.service';

describe('DataSearchConstListService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DataSearchConstListService]
    });
  });

  it('should be created', inject([DataSearchConstListService], (service: DataSearchConstListService) => {
    expect(service).toBeTruthy();
  }));
});
