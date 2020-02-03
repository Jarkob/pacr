import { Subscription, interval } from 'rxjs';
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

  benchmarkerSubscription: Subscription;
  benchmarkerUpdateInterval = 300;

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

}
