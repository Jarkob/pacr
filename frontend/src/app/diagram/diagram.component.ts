import { HttpErrorResponse } from '@angular/common/http';
import { ErrorComponent } from './../error/error.component';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { DetailViewService } from './../services/detail-view.service';
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
import * as Chart from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { BenchmarkGroup } from '../classes/benchmark-group';
import { LegendItem } from '../classes/legend-item';
import * as moment from 'moment';
import { MatDatepickerInputEvent, MatDialog } from '@angular/material';
import { DatePipe } from '@angular/common';
import { ShortenStringPipe } from '../pipes/shorten-string-pipe';


let tmp: any = null;

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
    private previewDialog: DiagramMaximizerService,
    private benchmarkingResultService: BenchmarkingResultService,
    private repositoryService: RepositoryService,
    private benchmarkService: BenchmarkService,
    private detailViewService: DetailViewService,
    private formBuilder: FormBuilder,
    private dialog: MatDialog
    private datePipe: DatePipe,
    private shortenString: ShortenStringPipe
  ) {
  }

  @Input() inSelectedBenchmark: Benchmark;
  @Input() inSelectedProperty: BenchmarkProperty;
  @Input() inSelectedDatasets: Dataset[];
  @Input() inRepositories: Map<number, Repository>;
  @Input() inBenchmarkGroups: BenchmarkGroup[];
  @Input() inBenchmarks: Map<string, Benchmark[]>;
  @Input() inRepositoryResults: Map<number, Map<string, any>>;

  /**
   * data
   */
  repositories: Map<number, Repository> = new Map<number, Repository>();
  benchmarkGroups: BenchmarkGroup[];
  benchmarks: Map<string, Benchmark[]> = new Map<string, Benchmark[]>();

  repositoryResults: Map<number, Map<string, Map<string, any>>> = new Map<number, Map<string, Map<string, any>>>();

  selectedBenchmark: Benchmark;
  selectedBenchmarkProperty: BenchmarkProperty;

  currentDate = new Date();

  until = moment().unix();
  from = moment().subtract(1, 'month').unix();

  groupFrom: FormGroup;
  groupUntil: FormGroup;

  // the lines that show up in the diagram
  lists: any[];

  @Input() maximized: boolean;
  dialogRef: DiagramMaximizedRef;

  // images
  errorImage = new Image(20, 20);
  globalErrorImage = new Image(20, 20);
  notYetImage = new Image(20, 20);
  noBenchmarks = new Image(20, 20);

  /**
   * diagram configuration
   */
  @ViewChild('mycanvas', {static: true}) chart: BaseChartDirective;

  options = {
    maintainAspectRatio: false,
    lineTension: 0,
    scales: {
      xAxes: [{
          type: 'time',
          time: {
              parser: 'YYYY-MM-DDTHH:mm:ss',
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
              labelString: this.selectedBenchmarkProperty ? this.selectedBenchmarkProperty.name : 'value' // FIXME doesn't work
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
      }
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
          .code[selected[0]._index].commitHash);

        // if no results available
        if (!this.datasets[selected[0]._datasetIndex].code[selected[0]._index].result || !this.datasets[selected[0]._datasetIndex]
          .code[selected[0]._index].result[this.selectedBenchmarkProperty.name]) {
          return;
        }

        // draw horizontal line
        this.addLine(this.datasets[selected[0]._datasetIndex]
          .code[selected[0]._index].result[this.selectedBenchmarkProperty.name].result);
      }
    },
    tooltips: {
      callbacks: {
        title: (items: any[], ) => {
          const commitHash = this.shortenString.transform(this.datasets[items[0].datasetIndex].code[items[0].index].commitHash, 7);
          
          return this.datasets[items[0].datasetIndex].repositoryName + ': ' + commitHash;
        },
        label: (item, data) => {
          // if there is an error, show it
          if (!this.datasets[item.datasetIndex].code[item.index].result) {
            return 'Not yet benchmarked';
          } else if (this.datasets[item.datasetIndex].code[item.index].globalError) {
            return 'Global Error: ' + this.datasets[item.datasetIndex].code[item.index].globalError;
          } else if (!this.datasets[item.datasetIndex].code[item.index].result) {
            return 'No results for this benchmark';
          } else if (Object.keys(this.datasets[item.datasetIndex].code[item.index].result).length === 0) {
            return 'No benchmarks available';
          } else if (this.datasets[item.datasetIndex].code[item.index].result[this.selectedBenchmarkProperty.name].errorMessage) {
            return 'Error:' + this.datasets[item.datasetIndex].code[item.index].result[this.selectedBenchmarkProperty.name].errorMessage;
          }
          let label = '' + Math.round(item.yLabel * 100) / 100;
          label += ' ' + this.selectedBenchmarkProperty.unit;

          // add labels to diagram entry
          if (this.datasets[item.datasetIndex].code[item.index].labels.length !== 0) {
            label += ' Labels: ';
            let prefix = '';
            for (const el of this.datasets[item.datasetIndex].code[item.index].labels) {
              label += prefix + el;
              prefix = ', ';
            }
          }
          return label;
        },
        afterLabel: (item, data) => {
          return 'authored: ' + this.datePipe.transform(this.datasets[item.datasetIndex].code[item.index].authorDate, 'dd.MM.yyyy');
        }
      }
    }
  };
  plugins = [
    ChartAnnotation,
    {
      afterUpdate: (chart) => {
        // tslint:disable-next-line:prefer-for-of
        for (let i = 0; i < chart.config.data.datasets.length; i++) {
          const dataset: any = chart.config.data.datasets[i];
          // key of dataset._meta has a random name
          for (let j = 0; j < dataset._meta[Object.keys(dataset._meta)[0]].data.length; j++) {
            const element = dataset._meta[Object.keys(dataset._meta)[0]].data[j];
            if (!dataset.code[j].result) {
              element._model.pointStyle = this.notYetImage;
            } else if (dataset.code[j].globalError) {
              element._model.pointStyle = this.globalErrorImage;
            } else if (Object.keys(dataset.code[j].result).length === 0) {
              element._model.pointStyle = this.noBenchmarks;
            } else if (!dataset.code[j].result[this.selectedBenchmarkProperty.name]) {
              // do nothing
            } else if (dataset.code[j].result[this.selectedBenchmarkProperty.name].errorMessage) {
              element._model.pointStyle = this.errorImage;
            }
          }
        }
      }
    }
  ];
  labels = [];
  type = 'line';
  legend = true;
  datasets: Dataset[] = [{
    data: [],
    code: [],
    label: 'no data',
    fill: false,
    lineTension: 0,
    branch: '',
    repositoryId: 0,
    repositoryName: '',
    borderColor: '',
    pointBackgroundColor: '',
    hidden: true
  }];
  legendData: any;

  loading = true;


  ngOnInit() {
    this.errorImage.src = 'assets/clear.svg';
    this.globalErrorImage.src = 'assets/block.svg';
    this.notYetImage.src = 'assets/run.svg';
    this.noBenchmarks.src = 'assets/disabled.svg';
    this.groupUntil = this.formBuilder.group({
      dateFormCtrl: new FormControl(new Date(this.until * 1000))
    });
    this.groupFrom = this.formBuilder.group({
      dateFormCtrl: new FormControl(new Date(this.from * 1000))
    });
    if (!this.maximized) {
      this.getRepositories();
    } else {
      this.loading = false;
      this.selectedBenchmark = this.inSelectedBenchmark;
      this.selectedBenchmarkProperty = this.inSelectedProperty;
      this.datasets = this.inSelectedDatasets;

      this.benchmarks = this.inBenchmarks;
      this.benchmarkGroups = this.inBenchmarkGroups;
      this.repositories = this.inRepositories;
      this.repositoryResults = this.inRepositoryResults;

      this.chart.chart.update();
    }
  }

  /**
   * resets the zoom of the diagram
   */
  public resetZoom() {
    (this.chart.chart as any).resetZoom();
    this.chart.chart.update();
  }

  /**
   * select a commit from the diagram
   * @param commitHash the id of the commit
   */
  public selectCommit(commitHash: string) {
    this.detailViewService.selectCommit(commitHash);
  }

  /**
   * maximize the diagram component
   */
  public maximizeDiagram() {
    this.dialogRef = this.previewDialog.open({
      selectedBenchmark: this.selectedBenchmark,
      selectedProperty: this.selectedBenchmarkProperty,
      selectedDatasets: this.datasets,
      repositories: this.repositories,
      groups: this.benchmarkGroups,
      benchmarks: this.benchmarks,
      results: this.repositoryResults
    });
  }

  public changeFrom(event: MatDatepickerInputEvent<Date>) {
    this.from = moment(event.value).unix();
    this.loading = true;
    this.getBenchmarkingResults(Array.from(this.repositories.keys()), 0, 0, this.datasets.length);
  }

  public changeUntil(event: MatDatepickerInputEvent<Date>) {
    this.until = moment(event.value).unix();
    this.loading = true;
    this.getBenchmarkingResults(Array.from(this.repositories.keys()), 0, 0, this.datasets.length);
  }

  /**
   * toggle specific lines in the diagram
   * @param legendItem the legend item
   */
  public toggleLines(legendItem: LegendItem) {
    for (const datasetIndex of legendItem.datasetIndices) {
      this.chart.datasets[datasetIndex].hidden = !this.repositories.get(legendItem.repositoryId).checked.get(legendItem.branch);
      this.datasets[datasetIndex].hidden = !this.repositories.get(legendItem.repositoryId).checked.get(legendItem.branch);
    }
    this.chart.update();
  }

  /**
   * select a benchmark to be displayed in the diagram
   */
  public loadBenchmark(): void {
    if (this.selectedBenchmark == null) {
      return;
    }
    this.datasets = this.datasets.splice(0, 1);
    this.chart.datasets = this.chart.datasets.splice(0, 1);
    this.selectedBenchmarkProperty = this.selectedBenchmark.properties[0];

    this.loading = true;
    const repositoryIds = Array.from(this.repositories.keys());
    this.getBenchmarkingResults(repositoryIds, 0, 0,  1);

    this.deleteLine();
  }

  private getBenchmarkingResults(repositoryIds: number[], index: number, branchIndex: number, prevLength: number) {
    if (repositoryIds.length <= index) {
      // remove previous
      this.datasets = this.datasets.splice(prevLength);
      this.resetZoom();
      this.chart.update();
      this.loadProperty();
      return;
    }
    if (branchIndex >= this.repositories.get(repositoryIds[index]).trackedBranches.length) {
      this.getBenchmarkingResults(repositoryIds, index + 1, 0, prevLength);
      return;
    }
    const branch: string = this.repositories.get(repositoryIds[index]).trackedBranches[branchIndex];
    this.benchmarkingResultService.getBenchmarkingResults(
      this.selectedBenchmark.id,
      repositoryIds[index],
      this.repositories.get(repositoryIds[index]).trackedBranches[branchIndex],
      this.from,
      this.until
      ).subscribe(
      data => {
        console.log('results for branch ' + branch, data);
        if (!this.repositoryResults.get(repositoryIds[index])) {
          this.repositoryResults.set(repositoryIds[index], new Map<string, any>());
        }
        this.repositoryResults.get(repositoryIds[index]).set(branch, data);
        const lines = this.calculateLines(repositoryIds[index], branch);
        // both is important, otherwise event listening for change of legend gets messed up
        this.chart.datasets = this.chart.datasets.concat(lines);
        this.datasets = this.datasets.concat(lines);
        this.getBenchmarkingResults(repositoryIds, index, branchIndex + 1, prevLength);
      }
    );
  }

  /**
   * load the selected benchmark property
   */
  public loadProperty() {
    this.options.scales.yAxes[0].scaleLabel.labelString = this.selectedBenchmarkProperty.name;
    let last = 0;
    // tslint:disable-next-line:prefer-for-of
    for (let i = 0; i < this.datasets.length; i++) {
      for (let j = 0; j < this.datasets[i].data.length; j++) {
        if (!this.datasets[i].code[j].result) {
          this.datasets[i].data[j].y = last;
        } else if (Object.keys(this.datasets[i].code[j].result).length === 0) {
          // is empty
          this.datasets[i].data[j].y = last;
        } else if (this.datasets[i].code[j].result[this.selectedBenchmarkProperty.name].errorMessage && j > 0) {
          this.datasets[i].data[j].y = last;
        } else {
          this.datasets[i].data[j].y = this.datasets[i].code[j].result[this.selectedBenchmarkProperty.name].result;
          last = this.datasets[i].data[j].y;
        }
      }
      for (let j = this.datasets[i].data.length - 1; j > -1; j--) {
        if (!this.datasets[i].code[j].result) {
          this.datasets[i].data[j].y = last;
        } else if (Object.keys(this.datasets[i].code[j].result).length === 0) {
          // is empty
          this.datasets[i].data[j].y = last;
        } else if (this.datasets[i].code[j].result[this.selectedBenchmarkProperty.name].errorMessage && j > 0) {
          this.datasets[i].data[j].y = last;
        } else {
          this.datasets[i].data[j].y = this.datasets[i].code[j].result[this.selectedBenchmarkProperty.name].result;
          last = this.datasets[i].data[j].y;
        }
      }
    }
    // ugly hack, otherwise can't access datasets from inside legend handler
    tmp = this.datasets;
    this.legendData = this.chart.chart.generateLegend();
    this.loading = false;
  }

  private calculateLines(repositoryId: number, branch: string): any[] {
    const lines = [];
    const newestCommit = this.getNewestCommit(this.repositoryResults.get(repositoryId).get(branch));
    this.lists = [];
    const empty = [];
    for (const [, commit] of Object.entries(this.repositoryResults.get(repositoryId).get(branch))) {
      commit.marked = false;
    }
    if (newestCommit) {
      this.dfs(repositoryId, branch, this.repositoryResults.get(repositoryId).get(branch)[newestCommit], empty);
    } else {
      this.dialog.open(ErrorComponent, {
        data: new HttpErrorResponse({
          error: null,
          headers: null,
          status: null,
          statusText: ('No commits found for repository ' + this.repositories.get(repositoryId).name + ' on branch ' + branch)
        })
      });
    }
    let index = 0;
    for (const list of this.lists) {
      const dataset: Dataset = {
        data: [],
        code: [],
        label: '' + index,
        fill: false,
        lineTension: 0,
        repositoryId,
        repositoryName: this.repositories.get(repositoryId).name,
        branch,
        borderColor: this.repositories.get(repositoryId).color,
        pointBackgroundColor: this.repositories.get(repositoryId).color,
        hidden: !this.repositories.get(repositoryId).checked.get(branch)
      };
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

  private dfs(repositoryId: number, branch: string, current: any, list: any[]) {
    if (!current) {
      this.lists.push(list);
      return;
    }
    list.push(this.repositoryResults.get(repositoryId).get(branch)[current.commitHash]);
    if (current.parents.length === 0
      || current.marked === true || !this.repositoryResults.get(repositoryId).get(branch)[current.commitHash]) {
      current.marked = true;
      this.lists.push(list);
      return;
    }
    current.marked = true;
    for (const parent of current.parents) {
      const newList = JSON.parse(JSON.stringify(list));
      this.dfs(repositoryId, branch, this.repositoryResults.get(repositoryId).get(branch)[parent], newList);
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
      if (this.chart && this.chart.chart.canvas.id === instance.canvas.id) {
        instance.options.annotation.annotations = [];
      }
    });

    this.chart.update();
  }

  private legendCallback(currentChart: any): any {
    const map: Map<string, LegendItem> = new Map<string, LegendItem>();
    let index = 0;
    for (const dataset of tmp) {
      const key = dataset.repositoryId + '/' + dataset.branch;
      if (map.has(key)) {
        map.get(key).datasetIndices.push(index);
      } else {
        map.set(key, {
          repositoryId: dataset.repositoryId,
          repositoryName: dataset.repositoryName,
          branch: dataset.branch,
          datasetIndices: [index], // FIXME: highly likely to cause issues
          color: dataset.borderColor
        });
      }
      index++;
    }
    return Array.from(map.values());
  }

  private getRepositories(): void {
    this.repositoryService.getAllRepositories().subscribe(
      data => {
        data.forEach(repo => {
          repo.checked = new Map<string, boolean>();
          for (const el of repo.trackedBranches) {
            repo.checked.set(el, true);
          }
          this.repositories.set(repo.id, repo);
        });
        this.getBenchmarkGroups();
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
          if (!this.selectedBenchmark && data.length > 0) {
            this.selectedBenchmark = data[0];
            if (this.selectedBenchmark.properties && this.selectedBenchmark.properties.length > 0) {
              this.selectedBenchmarkProperty = this.selectedBenchmark.properties[0];
              this.loadBenchmark();
            }
          }
      });
    });
  }
}
