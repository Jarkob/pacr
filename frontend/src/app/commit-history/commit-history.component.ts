import { RepositoryService } from './../services/repository.service';
import { Component, OnInit } from '@angular/core';
import { Commit } from '../classes/commit';

@Component({
  selector: 'app-commit-history',
  templateUrl: './commit-history.component.html',
  styleUrls: ['./commit-history.component.css']
})
export class CommitHistoryComponent implements OnInit {

  constructor(
  ) { }

  private commits: Commit[];

  ngOnInit() {

  }

}
