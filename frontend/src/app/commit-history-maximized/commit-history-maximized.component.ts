import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { EventService } from './../services/event.service';
import { StringService } from './../services/strings.service';
import { CommitHistoryMaximizedRef } from './../commit-history/commit-history-maximized-ref';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-commit-history-maximized',
  templateUrl: './commit-history-maximized.component.html',
  styleUrls: ['./commit-history-maximized.component.css']
})
export class CommitHistoryMaximizedComponent implements OnInit {

  constructor(
    public dialogRef: CommitHistoryMaximizedRef,
    private stringService: StringService,
    private eventService: EventService
  ) { }

  strings: any;
  commits: CommitBenchmarkingResult[];

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

  public close() {
    this.dialogRef.close();
  }

}
