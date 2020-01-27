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

  ngOnInit() {
    this.stringService.getEventsStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

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

}
