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

  constructor() { }

  ngOnInit() {
  }

}
