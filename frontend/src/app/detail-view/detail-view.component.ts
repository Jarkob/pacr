import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
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
    private stringService: StringService
  ) { }

  strings: any;
  benchmarkingResult: CommitBenchmarkingResult;
  selectedCommitSubscription: Subscription;
  maximizedDetailViewOverlayRef: OverlayRef;

  // selectedBenchmarkProperty[0] is Benchmark,
  // selectedBenchmarkProperty[1] is BenchmarkProperty
  selectedBenchmarkProperty: any[];

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
  }

  public compareBenchmarkProperty(o1: any, o2: any): boolean {
    return o1[0].id === o2[0].id && o1[1].name === o2[1].name;
  }

  public selectCommit(commitHash: string): void {
    if (commitHash === null || commitHash === '') {
      return;
    }

    this.benchmarkingResultService.getBenchmarkingResultsForCommit(commitHash).subscribe(
      data => {
        this.benchmarkingResult = data;

        this.selected = true;

        this.selectedBenchmarkProperty = null;
        // preselect the first benchmark property
        if (this.benchmarkingResult.benchmarksList && this.benchmarkingResult.benchmarksList.length > 0) {
          const benchmark = this.benchmarkingResult.benchmarksList[0];

          if (benchmark.results && benchmark.results.length > 0) {
            this.selectedBenchmarkProperty = [benchmark, benchmark.results[0]];
          }
        }
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

  ngOnDestroy() {
    // prevent memory leak when component is destroyed
    this.selectedCommitSubscription.unsubscribe();
  }

}
