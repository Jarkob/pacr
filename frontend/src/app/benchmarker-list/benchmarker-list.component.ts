import { SystemEnvironment } from './../classes/system-environment';
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
    private benchmarkerService: BenchmarkerCommunicationService
  ) { }

    strings: any;
    benchmarkers: SystemEnvironment[];

  ngOnInit() {
    this.stringService.getBenchmarkerListStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
    this.benchmarkerService.getOnlineBenchmarkers().subscribe(
      data => {
        this.benchmarkers = data;
      }
    );
  }

}
