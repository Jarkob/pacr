import { BenchmarkGroup } from './../classes/benchmark-group';
import { BenchmarkService } from './../services/benchmark.service';
import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { StringService } from './../services/strings.service';
import { COMMIT_HASH_DATA } from './../detail-view/detail-view-maximized.tokens';
import { DetailViewMaximizedRef } from './../detail-view/detail-view-maximized-ref';
import { Component, Inject, HostListener, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material';

const ESCAPE_KEY = 27;

@Component({
  selector: 'app-detail-view-maximized',
  templateUrl: './detail-view-maximized.component.html',
  styleUrls: ['./detail-view-maximized.component.css']
})
export class DetailViewMaximizedComponent implements OnInit {

  strings: any;
  benchmarkingResult: CommitBenchmarkingResult;

  constructor(
    @Inject(COMMIT_HASH_DATA) public commitHash: string,
    public dialogRef: DetailViewMaximizedRef,
    private stringService: StringService,
    private benchmarkingResultService: BenchmarkingResultService
  ) {}

  @HostListener('document:keydown', ['$event']) handleKeydown(event: KeyboardEvent) {
    if (event.keyCode === ESCAPE_KEY) {
      this.dialogRef.close();
    }
  }

  ngOnInit() {
    this.stringService.getDetailViewStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
    this.benchmarkingResultService.getBenchmarkingResultsForCommit(this.commitHash).subscribe(
      data => {
        this.benchmarkingResult = data;
      }
    );
  }

  /**
   * close the maximized view
   */
  public close() {
    this.dialogRef.close();
  }
}
