import { StringService } from './../services/strings.service';
import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-commit-details',
  templateUrl: './commit-details.component.html',
  styleUrls: ['./commit-details.component.css']
})
export class CommitDetailsComponent implements OnInit {

  constructor(
    private stringService: StringService
  ) { }

  @Input() benchmarkingResult: CommitBenchmarkingResult;

  strings: any;

  ngOnInit() {
    this.stringService.getCommitDetailsStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
  }

}
