import { BenchmarkingResult } from './../classes/benchmarking-result';
import { Benchmark } from './../classes/benchmark';
import { Component, OnInit } from '@angular/core';
import { BenchmarkingResultService } from '../services/benchmarking-result.service';
import { MockService } from '../services/mock.service';
import 'chartjs-plugin-zoom';
import { RepositoryService } from '../services/repository.service';
import { Repository } from '../classes/repository';
import { BenchmarkService } from '../services/benchmark.service';

@Component({
  selector: 'app-diagram',
  templateUrl: './diagram.component.html',
  styleUrls: ['./diagram.component.css']
})
export class DiagramComponent implements OnInit {


  constructor(
    private benchmarkingResultService: BenchmarkingResultService,
    private repositoryService: RepositoryService,
    private benchmarkService: BenchmarkService
  ) { }

  repositories: Repository[];
  benchmarks: Benchmark[];
  repositoryResults: Map<string, BenchmarkingResult[]> = new Map<string, BenchmarkingResult[]>();

  selectedBenchmark: Benchmark;

  public timeFormat = 'DD/MM/YYYY';

  public options = {
    maintainAspectRatio: false,
    scales: {
      xAxes: [{
          type: 'time',
          time: {
              parser: this.timeFormat,
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
            beginAtZero: true
          }
      }]
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
      }
    }
  };

  public labels = [];
  public type = 'line';
  public legend = true;
  public datasets = [
    {data: [], label: 'no data', fill: false},
    // {data: [{x: '04/01/2014', y: 5}, {x: '30/03/2014', y: 3}, {x: '04/01/2015', y: 1}], label: 'repo 1', fill: false},
    // {data: [{x: '01/04/2014', y: 2}, {x: '01/10/2014', y: 2}, {x: '01/10/2015', y: 3}], label: 'repo 2', fill: false},
    // {data: [{x: '01/01/2014', y: 3}, {x: '02/02/2015', y: 2}], label: 'repo 3', fill: false}
  ];

  ngOnInit() {
    this.getRepositories();
  }

  public selectBenchmark(benchmark: string): void {
    this.datasets = [];
    // create datasets for benchmark
    for (let [repository, benchmarkingResults] of this.repositoryResults) {
      const dataset = {data: [], label: repository, fill: false};
      benchmarkingResults.forEach(benchmarkingResult => {
        benchmarkingResult.groups.forEach(group => {
          group.benchmarks.forEach(element => {
            if (element.originalName == benchmark) {
              dataset.data.push({x: benchmarkingResult.commitCommitDate, y: element.results[0].mean});
            }
          })
        });
      });
      this.datasets.push(dataset);
      console.log('dataset: ', dataset);
    }
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
    for (let [repository, benchmarkingResults] of this.repositoryResults) {
      benchmarkingResults.forEach(benchmarkingResult => {
        benchmarkingResult.groups.forEach(group => {
          group.benchmarks.forEach(element => {
            this.benchmarks.push(element);
          })
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
