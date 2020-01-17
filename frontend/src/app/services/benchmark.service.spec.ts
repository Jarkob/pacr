import { TestBed } from '@angular/core/testing';

import { BenchmarkService } from './benchmark.service';

describe('BenchmarkService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BenchmarkService = TestBed.get(BenchmarkService);
    expect(service).toBeTruthy();
  });
});
