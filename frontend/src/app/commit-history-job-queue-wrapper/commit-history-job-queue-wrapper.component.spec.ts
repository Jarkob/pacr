import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommitHistoryJobQueueWrapperComponent } from './commit-history-job-queue-wrapper.component';

describe('CommitHistoryJobQueueWrapperComponent', () => {
  let component: CommitHistoryJobQueueWrapperComponent;
  let fixture: ComponentFixture<CommitHistoryJobQueueWrapperComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommitHistoryJobQueueWrapperComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommitHistoryJobQueueWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
