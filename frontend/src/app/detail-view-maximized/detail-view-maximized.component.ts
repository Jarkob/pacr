import { BenchmarkingResultTableComponent } from './../benchmarking-result-table/benchmarking-result-table.component';
import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { StringService } from './../services/strings.service';
import { COMMIT_HASH_DATA } from './../detail-view/detail-view-maximized.tokens';
import { DetailViewMaximizedRef } from './../detail-view/detail-view-maximized-ref';
import { Component, Inject, HostListener, OnInit, ViewChild } from '@angular/core';

const ESCAPE_KEY = 27;

@Component({
  selector: 'app-detail-view-maximized',
  templateUrl: './detail-view-maximized.component.html',
  styleUrls: ['./detail-view-maximized.component.css']
})
export class DetailViewMaximizedComponent implements OnInit {

  strings: any;
  benchmarkingResult: CommitBenchmarkingResult;
  @ViewChild('resultTable', {static: false}) resultTable: BenchmarkingResultTableComponent;

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
    this.selectCommit(this.commitHash);
  }

  /**
   * select a commit for the detail view
   * @param commitHash the hash of the commit
   */
  public selectCommit(commitHash: string): void {
    if (commitHash === null || commitHash === '') {
      return;
    }

    this.commitHash = commitHash;

    if (this.resultTable) {
      this.resultTable.selectCommit(commitHash);
    }

    this.benchmarkingResultService.getBenchmarkingResultsForCommit(commitHash).subscribe(
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
