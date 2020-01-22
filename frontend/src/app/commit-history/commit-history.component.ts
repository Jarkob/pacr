import { EventService } from './../services/event.service';
import { Component, OnInit } from '@angular/core';
import { Commit } from '../classes/commit';

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
    private eventService: EventService
  ) { }

  commits: Commit[];

  ngOnInit() {
    this.eventService.getCommitHistory().subscribe(
      data => {
        this.commits = data;
      }
    );
  }

}
