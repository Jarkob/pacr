import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommitComparisonComponent } from './commit-comparison.component';

describe('CommitComparisonComponent', () => {
  let component: CommitComparisonComponent;
  let fixture: ComponentFixture<CommitComparisonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommitComparisonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommitComparisonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
