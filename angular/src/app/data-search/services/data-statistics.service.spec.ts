import { TestBed, inject } from '@angular/core/testing';

import { DataStatisticsService } from './data-statistics.service';

describe('DataStatisticsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DataStatisticsService]
    });
  });

  it('should be created', inject([DataStatisticsService], (service: DataStatisticsService) => {
    expect(service).toBeTruthy();
  }));
});
