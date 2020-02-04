import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BenchmarkingResultTableComponent } from './benchmarking-result-table.component';

describe('BenchmarkingResultTableComponent', () => {
  let component: BenchmarkingResultTableComponent;
  let fixture: ComponentFixture<BenchmarkingResultTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BenchmarkingResultTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BenchmarkingResultTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
