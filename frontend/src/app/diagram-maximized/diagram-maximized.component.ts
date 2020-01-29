import { Benchmark } from './../classes/benchmark';
import { SELECTED_BENCHMARK } from './../diagram/diagram-maximized.tokens';
import { DiagramMaximizedRef } from './../diagram/diagram-maximized-ref';
import { Component, OnInit, HostListener, Inject } from '@angular/core';

const ESCAPE_KEY = 27;

@Component({
  selector: 'app-diagram-maximized',
  templateUrl: './diagram-maximized.component.html',
  styleUrls: ['./diagram-maximized.component.css']
})
export class DiagramMaximizedComponent implements OnInit {

  constructor(
    public dialogRef: DiagramMaximizedRef,
    @Inject(SELECTED_BENCHMARK) public selectedBenchmark: Benchmark
  ) { }

  ngOnInit() {
  }

  close() {
    this.dialogRef.close();
  }

  // FIXME @Daniel: Directive DetailViewMaximizedComponent, Property 'handleKeydown' is private
  // and only accessible within class 'DetailViewMaximizedComponent'.
  @HostListener('document:keydown', ['$event']) handleKeydown(event: KeyboardEvent) {
    if (event.keyCode === ESCAPE_KEY) {
      this.dialogRef.close();
    }
  }

}
