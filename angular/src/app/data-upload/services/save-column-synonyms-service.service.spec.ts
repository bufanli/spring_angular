import { TestBed, inject } from '@angular/core/testing';

import { SaveColumnSynonymsServiceService } from './save-column-synonyms-service.service';

describe('SaveColumnSynonymsServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SaveColumnSynonymsServiceService]
    });
  });

  it('should be created', inject([SaveColumnSynonymsServiceService], (service: SaveColumnSynonymsServiceService) => {
    expect(service).toBeTruthy();
  }));
});
