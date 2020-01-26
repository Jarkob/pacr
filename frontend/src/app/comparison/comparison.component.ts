import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { BenchmarkingResult } from './../classes/benchmarking-result';
import { Component, OnInit } from '@angular/core';

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
    private benchmarkingResultService: BenchmarkingResultService
  ) { }

  selectedCommits: BenchmarkingResult[] = [];

  ngOnInit() {
  }


  selectCommit(sha: string) {
    this.benchmarkingResultService.getBenchmarkingResultsForCommit(sha)
    .subscribe(
      data => {
        // todo
      }
    );
  }

  clear() {
    this.selectedCommits = [];
  }
}
