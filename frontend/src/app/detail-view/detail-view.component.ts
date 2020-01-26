import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { BenchmarkService } from './../services/benchmark.service';
import { Benchmark } from './../classes/benchmark';
import { BenchmarkProperty } from './../classes/benchmark-property';
import { BenchmarkingResult } from './../classes/benchmarking-result';
import { StringService } from './../services/strings.service';
import { DetailViewMaximizerService } from './detail-view-maximizer.service';
import { DetailViewMaximizedRef } from './detail-view-maximized-ref';
import { Subscription } from 'rxjs';
import { DiagramService } from './../services/diagram.service';
import { OverlayRef } from '@angular/cdk/overlay';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { Component, OnInit, OnDestroy } from '@angular/core';

/**
 * show the detail view of a commit and his benchmarking results
 */
@Component({
  selector: 'app-detail-view',
  templateUrl: './detail-view.component.html',
  styleUrls: ['./detail-view.component.css']
})
export class DetailViewComponent implements OnInit, OnDestroy {

  constructor(
    private previewDialog: DetailViewMaximizerService,
    private benchmarkingResultService: BenchmarkingResultService,
    private diagramService: DiagramService,
    private stringService: StringService,
    private benchmarkService: BenchmarkService
  ) { }

  strings: any;
  benchmarkingResult: CommitBenchmarkingResult;
  selectedCommitSubscription: Subscription;
  maximizedDetailViewOverlayRef: OverlayRef;

  // selectedBenchmarkProperty[0] is Benchmark,
  // selectedBenchmarkProperty[1] is BenchmarkProperty
  selectedBenchmarkProperty: any[];

  benchmarks: Benchmark[];

  selected = false;

  ngOnInit() {
    this.stringService.getDetailViewStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
    this.selectedCommitSubscription = this.diagramService.selectedCommit.subscribe(
      data => {
        this.selectCommit(data);
      }
    );

    this.getBenchmarks();
  }

  private selectCommit(sha: string): void {
    if (sha === null || sha === '') {
      return;
    }

    this.benchmarkingResultService.getBenchmarkingResultsForCommit(sha).subscribe(
      data => {
        this.benchmarkingResult = data;
        this.selected = true;
      }
    );
  }

  // https://blog.thoughtram.io/angular/2017/11/20/custom-overlays-with-angulars-cdk.html
  // https://blog.thoughtram.io/angular/2017/11/27/custom-overlays-with-angulars-cdk-part-two.html
  public maximizeDetailView() {
    const dialogRef: DetailViewMaximizedRef = this.previewDialog.open({
      commitHash: this.benchmarkingResult.commitHash
    });
  }

  private getBenchmarks(): void {
    this.benchmarkService.getAllBenchmarks().subscribe(
      data => {
        this.benchmarks = data;
        console.log(this.benchmarks[0].properties[0]);
      }
    );
  }


  ngOnDestroy() {
    // prevent memory leak when component is destroyed
    this.selectedCommitSubscription.unsubscribe();
  }

}
