import { StringService } from './../services/strings.service';
import { Repository } from './../classes/repository';
import { RepositoryService } from './../services/repository.service';
import { PageEvent } from '@angular/material';
import { Commit } from './../classes/commit';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { Component, OnInit, EventEmitter, Output } from '@angular/core';

/**
 * compares two commits
 */
@Component({
  selector: 'app-comparison',
  templateUrl: './comparison.component.html',
  styleUrls: ['./comparison.component.css']
})
export class ComparisonComponent implements OnInit {

  constructor(
    private stringService: StringService,
    private repositoryService: RepositoryService,
    private benchmarkingResultService: BenchmarkingResultService
  ) { }

  @Output() commitSelectedEvent = new EventEmitter();

  strings: any;

  displayedColumns: string[] = ['commitHash', 'commitDate', 'authorDate', 'commitMessage'];

  pageSizeOptions = [5, 10, 15, 20];

  commitHash1: string;
  commitHash2: string;
  replaceCommitHash1 = true;

  commitsPage: any;
  commits: Commit[] = [];
  commitsPageEvent: PageEvent = new PageEvent();

  repositories: Repository[];
  selectedRepository: Repository;

  ngOnInit() {
    this.stringService.getComparisonStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
    this.repositoryService.getAllRepositories().subscribe(
      data => {
        this.repositories = data;

        if (this.repositories && this.repositories.length > 0) {
          this.selectedRepository = this.repositories[0];

          this.getCommits(this.commitsPageEvent);
        }
      }
    );
  }

  openDetailView(commitHash: string) {
    this.commitSelectedEvent.emit(commitHash);
  }

  selectForComparison(row: any) {
    if (this.replaceCommitHash1) {
      this.commitHash1 = row.commitHash;
    } else {
      this.commitHash2 = row.commitHash;
    }

    this.replaceCommitHash1 = !this.replaceCommitHash1;
  }

  compareRepository(o1: any, o2: any): boolean {
    return o1.id === o2.id;
  }

  updateRepository() {
    console.log(this.selectedRepository);
    this.getCommits(new PageEvent());
  }

  getCommits(event: any): any {
    this.commits = [];

    this.repositoryService.getCommits(this.selectedRepository.id, event.pageIndex, event.pageSize).subscribe(
      data => {
        this.commitsPage = data;
        this.commits = data.content;
      }
    );

    return event;
  }

  openCommitComparison() {

  }

  clear() {
    this.commitHash1 = null;
    this.commitHash2 = null;
    this.replaceCommitHash1 = true;
  }
}
