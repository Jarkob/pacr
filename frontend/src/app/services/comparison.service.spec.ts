import { TestBed } from '@angular/core/testing';

import { ComparisonService } from './comparison.service';

describe('ComparisonService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ComparisonService = TestBed.get(ComparisonService);
    expect(service).toBeTruthy();
  });
});
