import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommitComparisonTableComponent } from './commit-comparison-table.component';

describe('CommitComparisonTableComponent', () => {
  let component: CommitComparisonTableComponent;
  let fixture: ComponentFixture<CommitComparisonTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommitComparisonTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommitComparisonTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
