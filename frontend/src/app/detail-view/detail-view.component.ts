import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { Component, OnInit } from '@angular/core';
import { Commit } from '../classes/commit';
import { Row } from '../classes/row';

@Component({
  selector: 'app-detail-view',
  templateUrl: './detail-view.component.html',
  styleUrls: ['./detail-view.component.css']
})
export class DetailViewComponent implements OnInit {

  constructor(
    private benchmarkingResultService: BenchmarkingResultService
  ) { }

  selected: boolean;
  tableData: Row[];
  columns: string[] = ['property', 'value'];

  ngOnInit() {
  }

  public selectCommit(sha: string): void {
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

}
