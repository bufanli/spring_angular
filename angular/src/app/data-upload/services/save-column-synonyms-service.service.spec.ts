import { TestBed, inject } from '@angular/core/testing';

import { SaveColumnSynonymsService } from './save-column-synonyms.service';

describe('SaveColumnSynonymsServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SaveColumnSynonymsService]
    });
  });

  it('should be created', inject([SaveColumnSynonymsService], (service: SaveColumnSynonymsService) => {
    expect(service).toBeTruthy();
  }));
});
