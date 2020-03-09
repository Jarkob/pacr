import { Subscription, interval } from 'rxjs';
import { PageEvent } from '@angular/material';
import { CommitHistoryItem } from './../classes/commit-history-item';
import { EventService } from './../services/event.service';
import { StringService } from './../services/strings.service';
import { CommitHistoryMaximizedRef } from './../commit-history/commit-history-maximized-ref';
import { Component, OnInit, HostListener } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

const ESCAPE_KEY = 27;

@Component({
  selector: 'app-commit-history-maximized',
  templateUrl: './commit-history-maximized.component.html',
  styleUrls: ['./commit-history-maximized.component.css']
})
export class CommitHistoryMaximizedComponent implements OnInit {

  constructor(
    public dialogRef: CommitHistoryMaximizedRef,
    private stringService: StringService,
    private eventService: EventService,
    private cookieService: CookieService
  ) { }

  strings: any;

  commitsPage: any;
  commits: CommitHistoryItem[];
  commitsPageEvent: PageEvent = new PageEvent();

  pageSizeOptions = [5, 10, 15, 20, 50];

  commitHistoryInterval = 20; // in seconds
  commitHistorySubscription: Subscription;

  lastVisit: Date = null;

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

  /**
   * select a commit
   * @param commitHash the hash of the commit
   */
  @HostListener('document:keydown', ['$event']) handleKeydown(event: KeyboardEvent) {
    if (event.keyCode === ESCAPE_KEY) {
      this.dialogRef.close();
    }
  }

  public selectCommit(commitHash: string) {
    this.dialogRef.selectCommit(commitHash);
    this.close();
  }

  private updateLastVisit() {
    const cookieKey = 'last-visit';

    const lastVisit = this.cookieService.get(cookieKey);
    if (!isNaN(Date.parse(lastVisit))) {
      this.lastVisit = new Date(lastVisit);
    }
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
   * close the maximized component
   */
  public close() {
    this.dialogRef.close();
  }

}
