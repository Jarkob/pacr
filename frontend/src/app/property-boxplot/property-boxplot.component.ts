import { Subscription } from 'rxjs';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { DetailViewService } from './../services/detail-view.service';
import { BenchmarkGroup } from './../classes/benchmark-group';
import { BenchmarkService } from './../services/benchmark.service';
import { BenchmarkProperty } from './../classes/benchmark-property';
import { Benchmark } from './../classes/benchmark';
import { Component, OnInit, ElementRef, NgZone, OnChanges, SimpleChanges } from '@angular/core';
import { Chart, ChartData } from 'chart.js';
import 'chartjs-chart-box-and-violin-plot';

@Component({
  selector: 'app-property-boxplot',
  templateUrl: './property-boxplot.component.html',
  styleUrls: ['./property-boxplot.component.css']
})
export class PropertyBoxplotComponent implements OnInit, OnChanges {

  private readonly boxPlotData: ChartData = {
    datasets: [{
      label: 'Measurements',
      backgroundColor: '#ff8091',
      data: []
    }]
  };

  private chart: Chart;

  selectedBenchmark: Benchmark;
  selectedBenchmarkProperty: BenchmarkProperty;

  benchmarkGroups: BenchmarkGroup[];
  benchmarks: Map<string, Benchmark[]> = new Map<string, Benchmark[]>();

  selectedCommitSubscription: Subscription;
  commitHash: string;

  hasMeasurements = false;

  constructor(
    private readonly elementRef: ElementRef,
    private readonly ngZone: NgZone,
    private benchmarkService: BenchmarkService,
    private detailViewService: DetailViewService,
    private resultService: BenchmarkingResultService
  ) { }

  ngOnInit() {
    this.selectedCommitSubscription = this.detailViewService.selectedCommit.subscribe(
      data => {
        this.commitHash = data;
        this.selectProperty(this.selectedBenchmarkProperty);
      }
    );

    this.getBenchmarkGroups();
    this.build();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (!this.chart) {
      return;
    }

    // TODO handle updates

    this.chart.update();
  }

  private build() {
    this.ngZone.runOutsideAngular(() => {
      const node: HTMLElement = this.elementRef.nativeElement;
      this.chart = new Chart(node.querySelector('canvas'), {
        type: 'boxplot',
        options: {
          maintainAspectRatio: false,
        },
        data: this.boxPlotData
      });
    });
  }

  private getBenchmarkGroups() {
    this.benchmarkService.getAllGroups().subscribe(data => {
      this.benchmarkGroups = data;
      this.getBenchmarks();
    });
  }

  private getBenchmarks() {
    this.benchmarkGroups.forEach(group => {
      this.benchmarkService.getBenchmarksByGroup(group.id).subscribe(
        data => {
          this.benchmarks.set(group.name, data);

          if (data && data.length > 0) {
            this.selectBenchmark(data[0]);
          }
      });
    });
  }

  public selectBenchmark(benchmark: Benchmark) {
    if (benchmark) {
      this.selectedBenchmark = benchmark;

      if (benchmark.properties && benchmark.properties.length > 0) {
        this.selectProperty(benchmark.properties[0]);
      }
    }
  }

  public selectProperty(property: BenchmarkProperty) {
    if (property) {
      this.selectedBenchmarkProperty = property;

      this.resultService.getBenchmarkPropertyMeasurements(this.commitHash, this.selectedBenchmark.id, property.name).subscribe(
        data => {
          this.hasMeasurements = data && data.length > 0;

          this.chart.data.datasets[0].data = [];
          this.chart.data.datasets[0].data.push(data);

          this.chart.data.labels = [];

          if (this.hasMeasurements) {
            this.chart.data.labels.push(property.name + ' (' + (property.unit ? property.unit : 'no unit') + ')');
          }

          this.chart.update();
        }
      );
    }
  }

}
