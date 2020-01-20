import { Event } from './../classes/event';
import { Component, OnInit } from '@angular/core';
import { EventService } from '../services/event.service';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

  constructor(
    private eventService: EventService
  ) { }

  benchmarkingEvents: Event[];
  leaderboardEvents: Event[];

  ngOnInit() {
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
