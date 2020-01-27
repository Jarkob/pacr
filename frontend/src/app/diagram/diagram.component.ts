import { Dataset } from './../classes/dataset';
import { DiagramMaximizerService } from './diagram-maximizer.service';
import { DiagramMaximizedRef } from './diagram-maximized-ref';
import { BenchmarkingResult } from './../classes/benchmarking-result';
import { Benchmark } from './../classes/benchmark';
import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { BenchmarkingResultService } from '../services/benchmarking-result.service';
import 'chartjs-plugin-zoom';
import * as ChartAnnotation from 'chartjs-plugin-annotation';
import { RepositoryService } from '../services/repository.service';
import { Repository } from '../classes/repository';
import { BenchmarkService } from '../services/benchmark.service';
import { DiagramService } from '../services/diagram.service';
import * as Chart from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { BenchmarkGroup } from '../classes/benchmark-group';
import { LegendItem } from '../classes/legend-item';

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
    private diagramService: DiagramService,
  ) { }

  /**
   * data
   */
  repositories: Repository[];
  benchmarkGroups: BenchmarkGroup[];
  benchmarks: Map<string, Benchmark[]> = new Map<string, Benchmark[]>();
  repositoryResults: Map<string, BenchmarkingResult[]> = new Map<string, BenchmarkingResult[]>();
  selectedBenchmark: Benchmark;

  // TODO fix types
  commits: any;
  // the lines that show up in the diagram
  lists: any[];
  // if a list should show up in the diagram
  checked: boolean[];


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
    legend: {
      position: 'left',
      align: 'start',
      onClick: (evt, item) => {
        // FIXME doesn#t work
        console.log('selected ', item);
      },
      labels: {
        boxWidth: 12
      },
      display: false
    },
    legendCallback: this.legendCallback,
    onClick: (evt, item) => {
      console.log('evt: ', evt);
    //   var index = legendItem.datasetIndex;
    //   var ci = this.chart;
    //   var meta = ci.getDatasetMeta(index);

    //   // See controller.isDatasetVisible comment
    //   meta.hidden = meta.hidden === null ? !ci.data.datasets[index].hidden : null;

    // // We hid a dataset ... rerender the chart
    // ci.update();

      this.deleteLine();
      // if items were selected
      if (item.length !== 0) {
        const selected: any[] = this.chart.chart.getElementAtEvent(evt);
        this.selectCommit(this.datasets[selected[0]._datasetIndex]
          .code[selected[0]._index].sha);

        // draw horizontal line
        this.addLine(this.datasets[selected[0]._datasetIndex]
          .code[selected[0]._index].val);
      }
    }
  };
  labels = [];
  type = 'line';
  legend = true;
  datasets: Dataset[] = [{data: [], code: [], label: 'no data', fill: false, lineTension: 0, branch: '', repository: ''}];
  plugins = [ChartAnnotation];
  legendData: any;


  ngOnInit() {
    if (this.inSelectedBenchmark != null) {
      this.selectedBenchmark = this.inSelectedBenchmark;
    }

    this.getBenchmarkGroups();
    this.getRepositories();

    // get mock data
    this.benchmarkingResultService.getBenchmarkingResults('', '', '').subscribe(data => {
      this.commits = data;
    });
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

  public toggleLine(index: number) {
    const datasetIndex = this.legendData[index].datasetIndex;
    this.chart.datasets[datasetIndex].hidden = this.checked[index];
    this.chart.update();
  }

  /**
   * select a benchmark to be displayed in the diagram
   */
  public loadBenchmark(): void {
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
          // group.benchmarks.forEach(element => {
          //   if (element.originalName === this.selectedBenchmark.originalName) {
          //     dataset.data.push({
          //         x: benchmarkingResult.commitCommitDate,
          //         y: element.results[0].mean,
          //       });
          //     dataset.code.push({
          //       sha: benchmarkingResult.commitHash,
          //       val: element.results[0].mean
          //     });
          //   }
          // });
        });
      });
      // TODO sorting breaks code array, should be externally
      // dataset.data.sort((a, b) => {
      //   const x = new Date(a.x);
      //   const y = new Date(b.x);
      //   return x > y ? -1 : x < b ? 1 : 0;
      // });

      // this.datasets.push(dataset);
    }
    this.legendData = this.chart.chart.generateLegend();
    this.chart.update();
  }

  public loadMockData() {
    this.checked = [];
    this.datasets = [];
    const newestCommit = this.getNewestCommit(this.commits);
    this.lists = [];
    const empty = [];
    if (newestCommit) {
      this.dfs(newestCommit, empty);
    }
    let index = 0;
    for (const list of this.lists) {
      this.checked[index] = false;
      const dataset: Dataset = {data: [], code: [], label: '' + index, fill: false, lineTension: 0, repository: 'test', branch: 'master'};
      for (const commit of list) {
        dataset.data.push({
          x: commit.commitDate,
          y: commit.result
        });
        dataset.code.push({
          sha: commit.sha,
          val: commit.result
        });
        index++;
      }

      // both is important, otherwise event listening for change of legend gets messed up
      this.chart.datasets.push(dataset);
      this.datasets.push(dataset);
    }

    // has to remove empty dataset, otherwise legend does not work
    if (this.chart.datasets.length > this.lists.length) {
      this.chart.datasets.splice(0, 1);
    }

    this.legendData = this.chart.chart.generateLegend();
    this.chart.update();
  }


  private dfs(current, list: any[]) {
    list.push(current);
    if (current.parents.length === 0 || current.marked === true) {
      current.marked = true;
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

  private legendCallback(currentChart: any): any {
    const legend: LegendItem[] = [];
    let index = 0;
    for (const dataset of currentChart.data.datasets) {
      console.log('dataset: ', dataset);
      legend.push({
        repository: dataset.repository,
        branch: dataset.branch,
        datasetIndex: index // FIXME highly likely to cause issues
      });
      index++;
    }
    return legend;
  }

  private getRepositories(): void {
    this.repositoryService.getAllRepositories().subscribe(
      data => {
        this.repositories = data;
        // this.getBenchmarkingResults();
      }
    );
  }

  private getBenchmarkGroups(): void {
    this.benchmarkService.getAllGroups().subscribe(data => {
      this.benchmarkGroups = data;
      this.getBenchmarks();
    });
  }

  private getBenchmarks(): void {
    this.benchmarkGroups.forEach(group => {
      this.benchmarkService.getBenchmarksByGroup(group.id).subscribe(
        data => this.benchmarks.set(group.name, data)
      );
    });

    // if (this.benchmarks.length > 0 && this.selectedBenchmark == null) {
    //   this.selectedBenchmark = this.benchmarks[0];
    // }
    // this.loadBenchmark();
  }

}
