import { BenchmarkProperty } from './../classes/benchmark-property';
import { Dataset } from './../classes/dataset';
import { DiagramMaximizerService } from './diagram-maximizer.service';
import { DiagramMaximizedRef } from './diagram-maximized-ref';
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

  repositoryResults: Map<number, Map<string, any>> = new Map<number, Map<string, any>>();

  selectedBenchmark: Benchmark;
  selectedBenchmarkProperty: BenchmarkProperty;

  // the lines that show up in the diagram
  lists: any[];
  // if a list should show up in the diagram
  checked: boolean[] = [];


  @Input() maximized: boolean;
  dialogRef: DiagramMaximizedRef;

  /**
   * diagram configuration
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
          return this.datasets[items[0].datasetIndex].code[items[0].index].commitHash;
        },
        label: (item, data) => {
          // if there is an error, show it
          if (this.datasets[item.datasetIndex].code[item.index].globalError) {
            return 'Global Error: ' + this.datasets[item.datasetIndex].code[item.index];
          }
          if (this.datasets[item.datasetIndex].code[item.index].result[this.selectedBenchmarkProperty.name].errorMessage) {
            return 'Error:' + this.datasets[item.datasetIndex].code[item.index].result[this.selectedBenchmarkProperty.name].errorMessage;
          }
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
  datasets: Dataset[] = [{data: [], code: [], label: 'no data', fill: false, lineTension: 0, branch: '', repository: 0}];
  plugins = [ChartAnnotation];
  legendData: any;


  ngOnInit() {
    if (this.inSelectedBenchmark != null) {
      this.selectedBenchmark = this.inSelectedBenchmark;
    }

    this.getRepositories();
    this.getBenchmarkGroups();
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

    for (const repo of this.repositories) {
      this.benchmarkingResultService.getForBenchmarkAndRepository(this.selectedBenchmark.id, repo.id).subscribe(
        data => {
          this.repositoryResults.set(repo.id, data);
          const lines = this.calculateLines(repo.id);
          // both is important, otherwise event listening for change of legend gets messed up
          this.chart.datasets.concat(lines);
          this.datasets = lines;
          console.log('datasets: ', this.datasets);
        }
      );
    }
    this.deleteLine();
  }

  /**
   * load the selected benchmark property
   */
  public loadProperty() {
    // tslint:disable-next-line:prefer-for-of
    for (let i = 0; i < this.datasets.length; i++) {
      for (let j = 0; j < this.datasets[i].data.length; j++) {
        if (this.datasets[i].code[j].result[this.selectedBenchmarkProperty.name].errorMessage && j > 0) {
          this.datasets[i].data[j].y = this.datasets[i].data[j - 1].y;
        } else {
          this.datasets[i].data[j].y = this.datasets[i].code[j].result[this.selectedBenchmarkProperty.name].result;
        }
      }
    }

    // load images
    const errorImage = new Image(20, 20);
    const globalErrorImage = new Image(20, 20);
    errorImage.src = 'assets/warning.svg';
    globalErrorImage.src = 'assets/error.svg';
    Chart.pluginService.register({
      afterUpdate: (chart) => {
        // tslint:disable-next-line:prefer-for-of
        for (let i = 0; i < chart.config.data.datasets.length; i++) {
          const dataset: any = chart.config.data.datasets[i];
          for (let j = 0; j < dataset._meta[2].data.length; j++) {
            const element = dataset._meta[2].data[j];
            if (dataset.code[j].globalError) {
              element._model.pointStyle = globalErrorImage;
            } else if (dataset.code[j].result[this.selectedBenchmarkProperty.name].errorMessage) {
              element._model.pointStyle = errorImage;
            }
          }
        }
      }
    });

    this.legendData = this.chart.chart.generateLegend();
    this.chart.update();
  }

  private calculateLines(repositoryId: number): any[] {
    const lines = [];
    const newestCommit = this.getNewestCommit(this.repositoryResults.get(repositoryId));
    this.lists = [];
    this.checked = [];
    const empty = [];
    if (newestCommit) {
      this.dfs(repositoryId, this.repositoryResults.get(repositoryId)[newestCommit], empty);
    } else {
      console.error('No newest commit found');
    }
    let index = 0;
    for (const list of this.lists) {
      this.checked[index] = true;
      const dataset: Dataset = {
        data: [], code: [], label: '' + index, fill: false, lineTension: 0, repository: repositoryId, branch: 'master'};
      for (const commit of list) {
        dataset.data.push({
          x: commit.commitDate,
        });
        dataset.code.push(commit);
      }
      index++;
      lines.push(dataset);
    }
    return lines;
  }

  private dfs(repositoryId: number, current: any, list: any[]) {
    list.push(this.repositoryResults.get(repositoryId)[current.commitHash]);
    if (current.parents.length === 0 || current.marked === true) {
      current.marked = true;
      this.lists.push(list);
      return;
    }
    current.marked = true;
    for (const parent of current.parents) {
      const newList = JSON.parse(JSON.stringify(list));
      this.dfs(repositoryId, this.repositoryResults.get(repositoryId)[parent], newList);
    }
  }

  private getNewestCommit(results: Map<string, any>): any {
    let newestDate: Date = new Date('1950-01-01');
    let newest = null;
    for (const [sha, result] of Object.entries(results)) {
      if (new Date(result.commitDate) > newestDate) {
        newestDate = new Date(result.commitDate);
        newest = sha;
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
        datasetIndex: index // FIXME: highly likely to cause issues
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
        }
      );
    });
  }

}
