import { CookieService } from 'ngx-cookie-service';
import { PageEvent } from '@angular/material';
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
    private stringService: StringService,
    private cookieService: CookieService
  ) { }

  commitsPage: any;
  commits: CommitHistoryItem[];
  commitsPageEvent: PageEvent = new PageEvent();

  pageSizeOptions = [5, 10, 15, 20, 50];

  strings: any;

  commitHistoryInterval = 5; // in seconds
  commitHistorySubscription: Subscription;

  lastVisit: Date = null;

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

    this.commitsPageEvent.pageIndex = 0;

    this.updateLastVisit();

    this.getCommitHistory(this.commitsPageEvent);
    this.commitHistorySubscription = interval(this.commitHistoryInterval * 1000).subscribe(
      val => {
        if (this.commitsPageEvent.pageIndex === 0)  {
          this.getCommitHistory(this.commitsPageEvent);
        }
      }
    );
  }

  private updateLastVisit() {
    const cookieKey = 'last-visit';

    const lastVisit = this.cookieService.get(cookieKey);
    if (!isNaN(Date.parse(lastVisit))) {
      this.lastVisit = new Date(lastVisit);
    }

    this.cookieService.set(cookieKey, new Date().toISOString());
  }

  public isNewCommit(commit: CommitHistoryItem): boolean {
    if (!this.lastVisit || !commit) {
      return true;
    }
    
    return this.lastVisit < new Date(commit.entryDate);
  }

  /**
   * Fetches all available latest commits in a page.
   *
   * @param pagingEvent pageable event containing paging information.
   * @returns the pagination event.
   */
  public getCommitHistory(event: any): any {
    this.eventService.getCommitHistory(event.pageIndex, event.pageSize).subscribe(
      data => {
        this.commitsPage = data;
        this.commits = data.content;
      }
    );

    return event;
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
