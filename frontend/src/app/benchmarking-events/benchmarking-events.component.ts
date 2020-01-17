import { Event } from './../classes/event';
import { EventService } from './../services/event.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-benchmarking-events',
  templateUrl: './benchmarking-events.component.html',
  styleUrls: ['./benchmarking-events.component.css']
})
export class BenchmarkingEventsComponent implements OnInit {

  constructor(
    private eventService: EventService
  ) { }

  public events: Event[];

  ngOnInit() {
    this.eventService.getBenchmarkingEvents().subscribe(
      data => {
        this.events = data;
      }
    );
  }

}
