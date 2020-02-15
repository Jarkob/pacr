import { Subscription, interval } from 'rxjs';
import { CommitHistoryItem } from './../classes/commit-history-item';
import { DetailViewService } from './../services/detail-view.service';
import { StringService } from './../services/strings.service';
import { CommitHistoryMaximizerService } from './commit-history-maximizer.service';
import { CommitHistoryMaximizedRef } from './commit-history-maximized-ref';
import { EventService } from './../services/event.service';
import { Component, OnInit } from '@angular/core';

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

  strings: any;
  commits: CommitHistoryItem[];

  commitHistoryInterval = 20; // in seconds
  commitHistorySubscription: Subscription;

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

    this.getCommitHistory();
    this.commitHistorySubscription = interval(this.commitHistoryInterval * 1000).subscribe(
      val => {
        this.getCommitHistory();
      }
    );
  }

  private getCommitHistory() {
    this.eventService.getCommitHistory().subscribe(
      data => {
        this.commits = data;
      }
    );
  }

  /**
   * Assign each displayed commit history item a unique identifier so it doesn't need to
   * be rendered again if it doesn't change.
   * 
   * @param index index of the item in the list.
   * @param item the commit history item.
   */
  public trackCommitHistoryItem(index: number, item: CommitHistoryItem) {
    return item.commitHash;
  }

  /**
   * maximize the commit history component
   */
  public maximizeCommitHistory() {
    const dialogRef: CommitHistoryMaximizedRef = this.previewDialog.open(this);
  }
}
