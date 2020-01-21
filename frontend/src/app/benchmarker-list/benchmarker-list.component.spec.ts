import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BenchmarkerListComponent } from './benchmarker-list.component';

describe('BenchmarkerListComponent', () => {
  let component: BenchmarkerListComponent;
  let fixture: ComponentFixture<BenchmarkerListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BenchmarkerListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BenchmarkerListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
