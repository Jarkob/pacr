import { Subscription, interval } from 'rxjs';
import { StringService } from './../services/strings.service';
import { Event } from './../classes/event';
import { Component, OnInit } from '@angular/core';
import { EventService } from '../services/event.service';

/**
 * lists the events emitted by the backend
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

  benchmarkingEvents: Event[];
  leaderboardEvents: Event[];

  eventSubscription: Subscription;
  eventUpdateInterval = 120; // in seconds

  ngOnInit() {
    this.stringService.getEventsStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    // perform initial load
    this.eventService.getBenchmarkingEvents().subscribe(
      data => {
        this.benchmarkingEvents = data;
      }
    );
    this.eventService.getLeaderboardEvents().subscribe(
      data => {
        this.leaderboardEvents = data;
      }
    );

    this.eventSubscription = interval(this.eventUpdateInterval * 1000).subscribe(
      () => {
        this.eventService.getBenchmarkingEvents().subscribe(
          data => {
            this.benchmarkingEvents = data;
          }
        );
        this.eventService.getLeaderboardEvents().subscribe(
          data => {
            this.leaderboardEvents = data;
          }
        );
      }
    );
  }

}
