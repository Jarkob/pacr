import { BenchmarkingResult } from './../classes/benchmarking-result';
import { Benchmark } from './../classes/benchmark';
import { Component, OnInit, HostBinding, ViewChild } from '@angular/core';
import { BenchmarkingResultService } from '../services/benchmarking-result.service';
import 'chartjs-plugin-zoom';
import * as ChartAnnotation from 'chartjs-plugin-annotation';
import { RepositoryService } from '../services/repository.service';
import { Repository } from '../classes/repository';
import { BenchmarkService } from '../services/benchmark.service';
import { DiagramService } from '../services/diagram.service';
import * as Chart from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

/**
 * displays benchmarking results in a line diagram
 */
@Component({
  selector: 'app-diagram',
  templateUrl: './diagram.component.html',
  styleUrls: ['./diagram.component.css']
})
export class DiagramComponent implements OnInit {

  constructor(
    private benchmarkingResultService: BenchmarkingResultService,
    private repositoryService: RepositoryService,
    private benchmarkService: BenchmarkService,
    private diagramService: DiagramService
  ) { }

  repositories: Repository[];
  benchmarks: Benchmark[];
  repositoryResults: Map<string, BenchmarkingResult[]> = new Map<string, BenchmarkingResult[]>();
  selectedBenchmark: Benchmark;


  /**
   * diagram stuff
   */
  @ViewChild(BaseChartDirective, { static: true }) chart: BaseChartDirective;
  options = {
    maintainAspectRatio: false,
    lineTension: 0.3,
    scales: {
      xAxes: [{
          type: 'time',
          time: {
              parser: 'DD/MM/YYYY',
              tooltipFormat: 'll'
          },
          scaleLabel: {
              display: true,
              labelString: 'Date'
          }
      }],
      yAxes: [{
          scaleLabel: {
              display: true,
              labelString: 'value'
          },
          ticks: {
            beginAtZero: true,
            suggestedMin: 0
          }
      }]
    },
    annotation: {
      annotations: []
    },
    plugins: {
      zoom: {
        pan: {
          enabled: true,
          mode: 'xy'
        },
        zoom: {
          enabled: true,
          mode: 'xy'
        }
      },
    },
    onClick: (evt, item) => {
      this.deleteLine();

      if (item.length !== 0) {
        this.selectCommit(this.datasets[item[0]._datasetIndex].code[item[0]._index].sha);

        // draw horizontal line
        this.addLine(this.datasets[item[0]._datasetIndex].code[item[0]._index].val);
      }
    }
  };
  labels = [];
  type = 'line';
  legend = true;
  datasets = [
    {data: [], code: [], label: 'no data', fill: false},
  ];
  plugins = [ChartAnnotation];


  ngOnInit() {
    this.getRepositories();
  }

  /**
   * select a commit from the diagram
   * @param sha the id of the commit
   */
  public selectCommit(sha: string) {
    this.diagramService.selectCommit(sha);
  }

  /**
   * select a benchmark to be displayed in the diagram
   */
  public selectBenchmark(benchmark: string): void {
    this.deleteLine();
    this.diagramService.selectCommit('');
    this.datasets = [];
    // create datasets for benchmark
    for (const [repository, benchmarkingResults] of this.repositoryResults) {
      const dataset = {data: [], code: [], label: repository, fill: false};
      benchmarkingResults.forEach(benchmarkingResult => {
        benchmarkingResult.groups.forEach(group => {
          group.benchmarks.forEach(element => {
            if (element.originalName === benchmark) {
              dataset.data.push({
                  x: benchmarkingResult.commitCommitDate,
                  y: element.results[0].mean,
                });
              dataset.code.push({
                sha: benchmarkingResult.commitHash,
                val: element.results[0].mean
              });
            }
          });
        });
      });
      this.datasets.push(dataset);
    }
  }

  private addLine(y: number) {
    Chart.helpers.each(Chart.instances, (instance) => {
      if (this.chart.chart.canvas.id === instance.canvas.id) {
        instance.options.annotation.annotations.push({
          type: 'line',
          mode: 'horizontal',
          scaleID: 'y-axis-0',
          value: y,
          borderColor: 'rgb(255,255,255)',
          borderWidth: 2,
          enabled: true
        });
      }
    });
    this.chart.update();
  }

  private deleteLine() {
    Chart.helpers.each(Chart.instances, (instance) => {
      if (this.chart.chart.canvas.id === instance.canvas.id) {
        instance.options.annotation.annotations = [];
      }
    });
    this.chart.update();
  }

  private getRepositories(): void {
    this.repositoryService.getAllRepositories().subscribe(
      data => {
        this.repositories = data;
        this.getBenchmarkingResults();
      }
    );
  }

  private getBenchmarks(): void {
    this.benchmarkService.getAllBenchmarks().subscribe(
      data => {
        this.benchmarks = data;
        if (this.benchmarks.length > 0) {
          this.selectedBenchmark = this.benchmarks[0];
        }
      }
    );

    // for mocking purposes
    this.benchmarks = [];
    for (const [repository, benchmarkingResults] of this.repositoryResults) {
      benchmarkingResults.forEach(benchmarkingResult => {
        benchmarkingResult.groups.forEach(group => {
          group.benchmarks.forEach(element => {
            this.benchmarks.push(element);
          });
        });
      });
    }
  }

  private getBenchmarkingResults(): void {
    this.repositories.forEach(repository => {
      this.benchmarkingResultService.getBenchmarkingResultsFromRepository(repository.name)
      .subscribe(
        data => {
          this.repositoryResults.set(repository.name, data);
          this.getBenchmarks();
        }
      );
    });
  }
}
