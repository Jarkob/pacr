import { OutputBenchmark } from './../classes/output-benchmark';
import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { COMMIT_HASH_1_DATA, COMMIT_HASH_2_DATA } from './../comparison/commit-comparison.tokens';
import { CommitComparisonRef } from './../comparison/commit-comparison-ref';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { StringService } from './../services/strings.service';
import { Component, OnInit, Inject, HostListener } from '@angular/core';

const ESCAPE_KEY = 27;

@Component({
  selector: 'app-commit-comparison',
  templateUrl: './commit-comparison.component.html',
  styleUrls: ['./commit-comparison.component.css']
})
export class CommitComparisonComponent implements OnInit {

  constructor(
    @Inject(COMMIT_HASH_1_DATA) public commitHash1: string,
    @Inject(COMMIT_HASH_2_DATA) public commitHash2: string,
    public dialogRef: CommitComparisonRef,
    private stringService: StringService,
    private resultService: BenchmarkingResultService
  ) { }

  strings: any;

  benchmarkingResult1: CommitBenchmarkingResult;
  benchmarkingResult2: CommitBenchmarkingResult;

  ngOnInit() {
    this.stringService.getCommitComparisonStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    this.getBenchmarkingResults();
  }

  @HostListener('document:keydown', ['$event']) handleKeydown(event: KeyboardEvent) {
    if (event.keyCode === ESCAPE_KEY) {
      this.dialogRef.close();
    }
  }

  close() {
    this.dialogRef.close();
  }

  getBenchmarkingResults() {
    this.resultService.getBenchmarkingResultsForCommit(this.commitHash1).subscribe(
      data => {
        this.benchmarkingResult1 = data;
      }
    );

    this.resultService.getBenchmarkingResultsForCommit(this.commitHash2).subscribe(
      data => {
        this.benchmarkingResult2 = data;
      }
    );
  }

}
