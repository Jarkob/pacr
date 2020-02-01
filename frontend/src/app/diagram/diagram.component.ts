import { BenchmarkProperty } from './../classes/benchmark-property';
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
  selectedBenchmarkProperty: BenchmarkProperty;

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
      display: false
    },
    legendCallback: this.legendCallback,
    onClick: (evt, item) => {
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
    },
    tooltips: {
      callbacks: {
        title: (items: any[], data) => {
          return this.datasets[items[0].datasetIndex].code[items[0].index].sha;
        },
        label: (item, data) => {
          // round label
          let label = data.datasets[item.datasetIndex].label || '';
          if (label) {
              label += ': ';
          }
          label += Math.round(item.yLabel * 100) / 100;
          return label;
        }
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
    this.benchmarkingResultService.getBenchmarkingResults(-1, -1, '').subscribe(data => {
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

  /**
   * maximize the diagram component
   */
  public maximizeDiagram() {
    this.dialogRef = this.previewDialog.open({ selectedBenchmark: this.selectedBenchmark });
  }

  /**
   * toggle a specific line in the diagram
   * @param index the index of the legend item
   */
  public toggleLine(index: number) {
    const datasetIndex = this.legendData[index].datasetIndex;
    this.chart.datasets[datasetIndex].hidden = !this.checked[index];
    this.chart.update();
  }

  /**
   * select a benchmark to be displayed in the diagram
   */
  public loadBenchmark(): void {
    if (this.selectedBenchmark == null) {
      return;
    }

    // FIXME messes up diagram, include later
    // this.benchmarkingResultService.getBenchmarkingResultsForBenchmark(this.selectedBenchmark.id).subscribe(
    //   data => this.commits = data
    // );

    this.deleteLine();
    this.diagramService.selectCommit('');
    // this.datasets = []; s. a.

    const lines = this.calculateLines('');
    // both is important, otherwise event listening for change of legend gets messed up
    this.chart.datasets.concat(lines);
    this.datasets.concat(lines);

    this.legendData = this.chart.chart.generateLegend();
    this.chart.update();
  }

  /**
   * load the selected benchmark property
   */
  public loadProperty() {
    // change datasets
    const lines = this.calculateLines(this.selectedBenchmarkProperty.name);
    this.chart.datasets = lines;
    this.datasets = lines;

    this.chart.update();
  }

  /**
   * load mock data
   */
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
      this.checked[index] = true;
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
      }
      index++;

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

  private calculateLines(property: string): any[] {
    const lines = [];
    const newestCommit = this.getNewestCommit(this.commits);
    this.lists = [];
    this.checked = [];
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
          y: commit.result// TODO add property
        });
        dataset.code.push({
          sha: commit.sha,
          val: commit.result// TODO add property
        });
        index++;
      }
      lines.push(dataset);
    }
    return lines;
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
        data => {
          this.benchmarks.set(group.name, data);
          console.log(data);
        }
      );
    });

    // if (this.benchmarks.length > 0 && this.selectedBenchmark == null) {
    //   this.selectedBenchmark = this.benchmarks[0];
    // }
    // this.loadBenchmark();
  }

}
