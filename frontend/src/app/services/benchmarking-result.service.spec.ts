import { TestBed } from '@angular/core/testing';

import { BenchmarkingResultService } from './benchmarking-result.service';

describe('BenchmarkingResultService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BenchmarkingResultService = TestBed.get(BenchmarkingResultService);
    expect(service).toBeTruthy();
  });
});
