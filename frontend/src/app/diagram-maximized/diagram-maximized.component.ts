import { BenchmarkProperty } from './../classes/benchmark-property';
import { Benchmark } from './../classes/benchmark';
import { SELECTED_BENCHMARK, SELECTED_PROPERTY, SELECTED_DATASETS } from './../diagram/diagram-maximized.tokens';
import { DiagramMaximizedRef } from './../diagram/diagram-maximized-ref';
import { Component, OnInit, HostListener, Inject } from '@angular/core';
import { Dataset } from '../classes/dataset';

const ESCAPE_KEY = 27;

@Component({
  selector: 'app-diagram-maximized',
  templateUrl: './diagram-maximized.component.html',
  styleUrls: ['./diagram-maximized.component.css']
})
export class DiagramMaximizedComponent implements OnInit {

  constructor(
    public dialogRef: DiagramMaximizedRef,
    @Inject(SELECTED_BENCHMARK) public selectedBenchmark: Benchmark,
    @Inject(SELECTED_PROPERTY) public selectedProperty: BenchmarkProperty,
    @Inject(SELECTED_DATASETS) public selectedDatasets: Dataset[]
  ) { }

  ngOnInit() {
  }

  /**
   * close the maximized view
   */
  public close() {
    this.dialogRef.close();
  }

  @HostListener('document:keydown', ['$event']) handleKeydown(event: KeyboardEvent) {
    if (event.keyCode === ESCAPE_KEY) {
      this.dialogRef.close();
    }
  }

}
