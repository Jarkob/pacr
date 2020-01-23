import { DiagramMaximizerService } from './diagram-maximizer.service';
import { DiagramMaximizedRef } from './diagram-maximized-ref';
import { BenchmarkingResult } from './../classes/benchmarking-result';
import { Benchmark } from './../classes/benchmark';
import { Component, OnInit, HostBinding, ViewChild, Input } from '@angular/core';
import { BenchmarkingResultService } from '../services/benchmarking-result.service';
import 'chartjs-plugin-zoom';
import * as ChartAnnotation from 'chartjs-plugin-annotation';
import { RepositoryService } from '../services/repository.service';
import { Repository } from '../classes/repository';
import { BenchmarkService } from '../services/benchmark.service';
import { DiagramService } from '../services/diagram.service';
import * as Chart from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { element } from 'protractor';

/**
 * displays benchmarking results in a line diagram
 */
@Component({
  selector: 'app-diagram',
  templateUrl: './diagram.component.html',
  styleUrls: ['./diagram.component.css']
})
export class DiagramComponent implements OnInit {

  @Input() inSelectedBenchmark: Benchmark;

  constructor(
    private previewDialog: DiagramMaximizerService,
    private benchmarkingResultService: BenchmarkingResultService,
    private repositoryService: RepositoryService,
    private benchmarkService: BenchmarkService,
    private diagramService: DiagramService
  ) { }

  repositories: Repository[];
  benchmarks: Benchmark[];
  repositoryResults: Map<string, BenchmarkingResult[]> = new Map<string, BenchmarkingResult[]>();
  selectedBenchmark: Benchmark;
  @Input() maximized: boolean;
  dialogRef: DiagramMaximizedRef;

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
              parser: 'YYYY-MM-DD',
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
    {data: [], code: [], label: 'no data', fill: false, lineTension: 0},
  ];
  plugins = [ChartAnnotation];


  ngOnInit() {
    if (this.inSelectedBenchmark != null) {
      this.selectedBenchmark = this.inSelectedBenchmark;
    }

    this.getRepositories();

    // get mock
    // and branch!
    this.benchmarkingResultService.getBenchmarkingResultsForBenchmark('test').subscribe(
      data => {
        this.commits = data;
      }
    );
  }

  /**
   * select a commit from the diagram
   * @param sha the id of the commit
   */
  public selectCommit(sha: string) {
    this.diagramService.selectCommit(sha);
  }

  public maximizeDiagram() {
    this.dialogRef = this.previewDialog.open({ selectedBenchmark: this.selectedBenchmark });
  }

  /**
   * select a benchmark to be displayed in the diagram
   */
  public loadBenchmark(): void {
    console.log(this.selectedBenchmark.customName);
    if (this.selectedBenchmark == null) {
      return;
    }

    this.deleteLine();
    this.diagramService.selectCommit('');
    this.datasets = [];

    // create datasets for benchmark
    for (const [repository, benchmarkingResults] of this.repositoryResults) {
      const dataset = {data: [], code: [], label: repository, fill: false, lineTension: 0};
      benchmarkingResults.forEach(benchmarkingResult => {
        benchmarkingResult.groups.forEach(group => {
          // tslint:disable-next-line:no-shadowed-variable
          group.benchmarks.forEach(element => {
            if (element.originalName === this.selectedBenchmark.originalName) {
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
      // TODO sorting breaks code array, should be externally
      dataset.data.sort((a, b) => {
        const x = new Date(a.x);
        const y = new Date(b.x);
        return x > y ? -1 : x < b ? 1 : 0;
      });
      this.datasets.push(dataset);
    }
  }

  public loadMockData() {
    this.datasets = [];
    const newestCommit = this.getNewestCommit(this.commits);
    this.lists = [];
    const empty = [];
    this.dfs(newestCommit, empty);
    let index = 0;
    for (const list of this.lists) {
      const dataset = {data: [], code: [], label: '' + index++, fill: false, lineTension: 0};
      for (const commit of list) {
        dataset.data.push({
          x: commit.commitDate,
          y: commit.result
        });
        dataset.code.push({
          sha: commit.sha,
          val: commit.result
        });
      }
      this.datasets.push(dataset);
    }
  }

  // tslint:disable-next-line:member-ordering
  commits: any;
  // tslint:disable-next-line:member-ordering
  lists: any[];

  private dfs(current, list: any[]) {
    list.push(current);
    if (current.parents.length === 0 || current.marked === true) {
      current.marked = true;
      console.log('list: ', list);
      this.lists.push(list);
      return;
    }
    current.marked = true;
    for (const parent of current.parents) {
      const newList = JSON.parse(JSON.stringify(list));
      this.dfs(this.commits[parent], newList);
    }
  }

  private getNewestCommit(commits: any): any {
    let newestDate: Date = new Date('1950-01-01');
    let newest = null;
    for (const sha in commits) {
      if (new Date(commits[sha].commitDate) > newestDate) {
        newestDate = new Date(commits[sha].commitDate);
        newest = commits[sha];
      }
    }
    return newest;
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

        if (this.benchmarks.length > 0 && this.selectedBenchmark == null) {
          this.selectedBenchmark = this.benchmarks[0];
        }

        this.loadBenchmark();
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
