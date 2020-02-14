import { DetailViewService } from './../services/detail-view.service';
import { StringService } from './../services/strings.service';
import { CommitHistoryMaximizerService } from './commit-history-maximizer.service';
import { CommitHistoryMaximizedRef } from './commit-history-maximized-ref';
import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { EventService } from './../services/event.service';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';

/**
 * shows the commit history
 */
@Component({
  selector: 'app-commit-history',
  templateUrl: './commit-history.component.html',
  styleUrls: ['./commit-history.component.css']
})
export class CommitHistoryComponent implements OnInit {

  constructor(
    private detailViewService: DetailViewService,
    private eventService: EventService,
    private previewDialog: CommitHistoryMaximizerService,
    private stringService: StringService
  ) { }

  @Output() commitSelectedEvent = new EventEmitter();
  strings: any;
  commits: CommitBenchmarkingResult[];

  /**
   * select a commit
   * @param commitHash the hash of the commit to be selected
   */
  public selectCommit(commitHash: string) {
    this.detailViewService.selectCommit(commitHash);
  }

  ngOnInit() {
    this.stringService.getCommitHistoryStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
    this.eventService.getCommitHistory().subscribe(
      data => {
        this.commits = data;
      }
    );
  }

  /**
   * maximize the commit history component
   */
  public maximizeCommitHistory() {
    const dialogRef: CommitHistoryMaximizedRef = this.previewDialog.open(this);
  }
}
