import { PageEvent } from '@angular/material';
import { Subscription, interval } from 'rxjs';
import { StringService } from './../services/strings.service';
import { Event } from './../classes/event';
import { Component, OnInit } from '@angular/core';
import { EventService } from '../services/event.service';

/**
 * lists the events emitted by the backend.
 * Events are categorized in benchmarking and leaderboard events.
 */
@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

  constructor(
    private eventService: EventService,
    private stringService: StringService
  ) { }

  strings: any;

  benchmarkingPage: any;
  benchmarkingEvents: Event[];
  benchmarkingPageEvent: PageEvent = new PageEvent();

  leaderboardPage: any;
  leaderboardEvents: Event[];
  leaderboardPageEvent: PageEvent = new PageEvent();

  pageSizeOptions = [5, 10, 15, 20];

  eventSubscription: Subscription;
  eventUpdateInterval = 20; // in seconds

  ngOnInit() {
    this.stringService.getEventsStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    // perform initial load
    this.getBenchmarkingEvents(this.benchmarkingPageEvent);
    this.getLeaderboardEvents(this.leaderboardPageEvent);

    this.eventSubscription = interval(this.eventUpdateInterval * 1000).subscribe(
      () => {
        this.getBenchmarkingEvents(this.benchmarkingPageEvent);
        this.getLeaderboardEvents(this.leaderboardPageEvent);
      }
    );
  }

  /**
   * Fetches all available benchmarking events in a page.
   *
   * @param pagingEvent pageable event containing paging information.
   * @returns the pagination event.
   */
  public getBenchmarkingEvents(event: any): any {
    this.eventService.getBenchmarkingEvents(event.pageIndex, event.pageSize).subscribe(
      data => {
        this.benchmarkingPage = data;
        this.benchmarkingEvents = data.content;
      }
    );

    return event;
  }

  /**
   * Fetches all available leaderboard events in a page.
   *
   * @param pagingEvent pageable event containing paging information.
   * @returns the pagination event.
   */
  public getLeaderboardEvents(pagingEvent: any): any {
    this.eventService.getLeaderboardEvents(pagingEvent.pageIndex, pagingEvent.pageSize).subscribe(
      data => {
        this.leaderboardPage = data;
        this.leaderboardEvents = data.content;
      }
    );

    return pagingEvent;
  }

  /**
   * Assign each displayed event a unique identifier so it doesn't need to
   * be rendered again if it doesn't change.
   *
   * @param index index of the item in the list.
   * @param item the event.
   */
  public trackCommitHistoryItem(index: number, item: Event) {
    return item.id;
  }

}
