import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-commit-history-job-queue-wrapper',
  templateUrl: './commit-history-job-queue-wrapper.component.html',
  styleUrls: ['./commit-history-job-queue-wrapper.component.css']
})
export class CommitHistoryJobQueueWrapperComponent implements OnInit {

  constructor() { }

  @Input() defaultTabIndex = 0;
  @Input() openDetailViewMaximized = false;

  ngOnInit() {
  }

}
