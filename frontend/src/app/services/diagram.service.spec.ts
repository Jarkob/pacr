import { TestBed } from '@angular/core/testing';

import { DiagramService } from './diagram.service';

describe('DiagramService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DiagramService = TestBed.get(DiagramService);
    expect(service).toBeTruthy();
  });
});
