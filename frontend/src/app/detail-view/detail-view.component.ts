import { Subscription } from 'rxjs';
import { DiagramService } from './../services/diagram.service';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { Component, OnInit, OnDestroy } from '@angular/core';
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

  constructor(
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

  ngOnDestroy() {
    // prevent memory leak when component is destroyed
    this.selectedCommitSubscription.unsubscribe();
  }
}
