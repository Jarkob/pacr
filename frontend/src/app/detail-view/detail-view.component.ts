import { DetailViewMaximizerService } from './detail-view-maximizer.service';
import { DetailViewMaximizedRef } from './detail-view-maximized-ref';
import { Subscription } from 'rxjs';
import { DiagramService } from './../services/diagram.service';
import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { Component, OnInit, OnDestroy, HostListener, Input } from '@angular/core';
import { Row } from '../classes/row';

/**
 * show the detail view of a commit and his benchmarking results
 */
@Component({
  selector: 'app-detail-view',
  templateUrl: './detail-view.component.html',
  styleUrls: ['./detail-view.component.css']
})
export class DetailViewComponent implements OnInit, OnDestroy {

  maximizedDetailViewOverlayRef: OverlayRef;

  constructor(
    private previewDialog: DetailViewMaximizerService,
    private benchmarkingResultService: BenchmarkingResultService,
    private diagramService: DiagramService
  ) { }

  selected = false;
  tableData: Row[];
  columns: string[] = ['property', 'value'];

  selectedCommitSha: string;
  selectedCommitSubscription: Subscription;

  ngOnInit() {
    this.selectedCommitSubscription = this.diagramService.selectedCommit.subscribe(
      data => {
        this.selectedCommitSha = data;
        this.selectCommit(this.selectedCommitSha);
      }
    );
  }

  private selectCommit(sha: string): void {
    if (sha === null || sha === '') {
      return;
    }
    this.benchmarkingResultService.getBenchmarkingResultsForCommit(sha).subscribe(
      data => {
        this.tableData = [
          {
            property: 'sha',
            value: data.commitHash,
          },
          {
            property: 'message',
            value: data.commitMessage
          },
          {
            property: 'entry-date',
            value: '' + data.commitEntryDate
          },
          {
            property: 'commit-date',
            value: '' + data.commitCommitDate
          },
          {
            property: 'author-date',
            value: '' + data.commitAuthorDate
          },
          {
            property: 'repository',
            value: data.commitRepositoryid
          },
          {
            property: 'branch',
            value: data.commitBranchName
          }
        ];
        this.selected = true;
      }
    );
  }

  // https://blog.thoughtram.io/angular/2017/11/20/custom-overlays-with-angulars-cdk.html
  // https://blog.thoughtram.io/angular/2017/11/27/custom-overlays-with-angulars-cdk-part-two.html
  public maximizeDetailView() {
    const dialogRef: DetailViewMaximizedRef = this.previewDialog.open({
      commitHash: this.selectedCommitSha
    });
  }


  ngOnDestroy() {
    // prevent memory leak when component is destroyed
    this.selectedCommitSubscription.unsubscribe();
  }

}
