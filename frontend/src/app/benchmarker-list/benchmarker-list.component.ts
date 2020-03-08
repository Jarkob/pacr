import { DetailViewMaximizerService } from './../detail-view/detail-view-maximizer.service';
import { DetailViewMaximizedRef } from './../detail-view/detail-view-maximized-ref';
import { DetailViewService } from './../services/detail-view.service';
import { Benchmarker } from './../classes/benchmarker';
import { Subscription, interval } from 'rxjs';
import { BenchmarkerCommunicationService } from './../services/benchmarker-communication.service';
import { StringService } from './../services/strings.service';
import { Component, OnInit } from '@angular/core';

/**
 * shows a list of the currently registered benchmarkers
 */
@Component({
  selector: 'app-benchmarker-list',
  templateUrl: './benchmarker-list.component.html',
  styleUrls: ['./benchmarker-list.component.css']
})
export class BenchmarkerListComponent implements OnInit {

  constructor(
    private stringService: StringService,
    private benchmarkerService: BenchmarkerCommunicationService,
    private detailViewService: DetailViewService,
    private previewDialog: DetailViewMaximizerService
  ) { }

  strings: any;
  benchmarkers: Benchmarker[];

  benchmarkerSubscription: Subscription;
  benchmarkerUpdateInterval = 15;

  ngOnInit() {
    this.stringService.getBenchmarkerListStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    // initial load
    this.benchmarkerService.getOnlineBenchmarkers().subscribe(
      data => {
        this.benchmarkers = data;
      }
    );

    this.benchmarkerSubscription = interval(this.benchmarkerUpdateInterval * 1000).subscribe(
      () => {
        this.benchmarkerService.getOnlineBenchmarkers().subscribe(
          data => {
            this.benchmarkers = data;
          }
        );
      }
    );
  }

  /**
   * Assign each displayed benchmarker a unique identifier so it doesn't need to
   * be rendered again if it doesn't change.
   *
   * @param index index of the item in the list.
   * @param item the benchmarker.
   */
  public trackBenchmarker(index: number, item: Benchmarker): string {
    return item.address;
  }

  public selectCommit(commitHash: string) {
    this.detailViewService.selectCommit(commitHash);
    this.maximizeDetailView(commitHash);
  }

  /**
   * maximize the detail view
   */
  public maximizeDetailView(selectedCommitHash: string) {
    const dialogRef: DetailViewMaximizedRef = this.previewDialog.open({
      commitHash: selectedCommitHash
    });
  }

}
