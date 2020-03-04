import { BenchmarkProperty } from './../classes/benchmark-property';
import { Injectable } from '@angular/core';
import { Benchmark } from '../classes/benchmark';
import { Dataset } from '../classes/dataset';

@Injectable({
  providedIn: 'root'
})
export class DiagramService {

  selectedBenchmark: Benchmark;
  selectedProperty: BenchmarkProperty;
  selectedDatasets: Dataset[] = [];

  constructor() { }

  public selected(): boolean {
    return this.selectedBenchmark != null && this.selectedProperty != null;
  }

  public selectBenchmark(benchmark: Benchmark) {
    this.selectedBenchmark = benchmark;
  }

  public selectBenchmarkProperty(property: BenchmarkProperty) {
    this.selectedProperty = property;
  }

  public selectDatasets(datasets: Dataset[]) {
    this.selectedDatasets = datasets;
  }
}
