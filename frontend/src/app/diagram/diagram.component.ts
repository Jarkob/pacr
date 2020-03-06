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
  ) { }

  @Input() inSelectedBenchmark: Benchmark;

  /**
   * data
   */
  repositories: Map<number, Repository> = new Map<number, Repository>();
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
          mode: 'xy',
          onPanComplete: ({ chart }) => {
            const ticks = chart.scales['x-axis-0']._ticks;
            console.log(ticks[0]);
            console.log(ticks[ticks.length - 1]);
          }
        },
        zoom: {
          enabled: true,
          mode: 'xy',
          onZoomComplete: ({ chart }) => {
            const ticks = chart.scales['x-axis-0']._ticks;
            console.log(ticks[0]);
            console.log(ticks[ticks.length - 1]);
          }
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
          .code[selected[0]._index].commitHash);

        // draw horizontal line
        this.addLine(this.datasets[selected[0]._datasetIndex]
          .code[selected[0]._index].result[this.selectedBenchmarkProperty.name].result);
      }
    },
    tooltips: {
      // enabled: false,
      // custom: tooltipModel => {
      //   // Tooltip Element
      //   let tooltipEl = document.getElementById('chartjs-tooltip');

      //   // Create element on first render
      //   if (!tooltipEl) {
      //       tooltipEl = document.createElement('div');
      //       tooltipEl.id = 'chartjs-tooltip';
      //       tooltipEl.innerHTML = '<table></table>';
      //       document.body.appendChild(tooltipEl);
      //   }

      //   // Hide if no tooltip
      //   if (tooltipModel.opacity === 0) {
      //       tooltipEl.style.opacity = '0';
      //       return;
      //   }

      //   // Set caret Position
      //   tooltipEl.classList.remove('above', 'below', 'no-transform');
      //   if (tooltipModel.yAlign) {
      //       tooltipEl.classList.add(tooltipModel.yAlign);
      //   } else {
      //       tooltipEl.classList.add('no-transform');
      //   }

      //   function getBody(bodyItem) {
      //       return bodyItem.lines;
      //   }

      //   // Set Text
      //   if (tooltipModel.body) {
      //     console.log('body: ', tooltipModel);
      //     const titleLines = tooltipModel.title || [];
      //     const bodyLines = tooltipModel.body.map(getBody);

      //     let innerHtml = '<thead>';

      //     titleLines.forEach(title => {
      //         innerHtml += '<tr><th>' + title + '</th></tr>';
      //     });
      //     innerHtml += '</thead><tbody>';

      //     bodyLines.forEach((body, i) => {
      //         const colors = tooltipModel.labelColors[i];
      //         let style = 'background:' + colors.backgroundColor;
      //         style += '; border-color:' + colors.borderColor;
      //         style += '; border-width: 2px';
      //         const span = '<span style="' + style + '"></span>';
      //         innerHtml += '<tr><td>' + span + body + '</td></tr>';
      //     });
      //     innerHtml += '</tbody>';

      //     const tableRoot = tooltipEl.querySelector('table');
      //     tableRoot.innerHTML = innerHtml;
      //   }

      //   // `this` will be the overall tooltip
      //   const position = this.chart.chart.canvas.getBoundingClientRect();

      //   // Display, position, and set styles for font
      //   tooltipEl.style.opacity = '1';
      //   tooltipEl.style.position = 'absolute';
      //   tooltipEl.style.left = position.left + window.pageXOffset + tooltipModel.caretX + 'px';
      //   tooltipEl.style.top = position.top + window.pageYOffset + tooltipModel.caretY + 'px';
      //   tooltipEl.style.fontFamily = tooltipModel._bodyFontFamily;
      //   tooltipEl.style.fontSize = tooltipModel.bodyFontSize + 'px';
      //   tooltipEl.style.fontStyle = tooltipModel._bodyFontStyle;
      //   tooltipEl.style.padding = tooltipModel.yPadding + 'px ' + tooltipModel.xPadding + 'px';
      //   tooltipEl.style.pointerEvents = 'none';
      // },

      callbacks: {
        title: (items: any[], ) => {
          return this.datasets[items[0].datasetIndex].repositoryName + ': '
          + this.datasets[items[0].datasetIndex].code[items[0].index].commitHash.substring(0, 8);
        },
        label: (item, data) => {
          // if there is an error, show it
          if (!this.datasets[item.datasetIndex].code[item.index].result) {
            return 'Not yet benchmarked';
          }
          if (this.datasets[item.datasetIndex].code[item.index].globalError) {
            return 'Global Error: ' + this.datasets[item.datasetIndex].code[item.index];
          }
          if (Object.keys(this.datasets[item.datasetIndex].code[item.index].result).length === 0) {
            return 'No benchmarks available';
          }
          if (this.datasets[item.datasetIndex].code[item.index].result[this.selectedBenchmarkProperty.name].errorMessage) {
            return 'Error:' + this.datasets[item.datasetIndex].code[item.index].result[this.selectedBenchmarkProperty.name].errorMessage;
          }
          let label = '' + Math.round(item.yLabel * 100) / 100;
          label += ' ' + this.selectedBenchmarkProperty.unit;

          label += '\n' + this.datasets[item.datasetIndex].code[item.index].author;
          label += '\n' + this.datasets[item.datasetIndex].code[item.index].authorDate;

          // add labels to diagram entry
          if (this.datasets[item.datasetIndex].code[item.index].labels.length !== 0) {
            label += '<br>Labels:';
            let prefix = '';
            for (const el of this.datasets[item.datasetIndex].code[item.index].labels) {
              label += prefix + el;
              prefix = ', ';
            }
          }
          return label;
        }
      }
    }
  };
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
    pointBackgroundColor: ''
  }];
  plugins = [ChartAnnotation];
  legendData: any;

  loading = false;


  ngOnInit() {
    this.chart.chart = undefined;
    this.getRepositories();
    if (this.inSelectedBenchmark != null) {
      this.selectedBenchmark = this.inSelectedBenchmark;
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
    this.dialogRef = this.previewDialog.open({ selectedBenchmark: this.selectedBenchmark });
  }

  /**
   * toggle specific lines in the diagram
   * @param index the index of the legend item
   */
  public toggleLines(index: number) {
    // tslint:disable-next-line:prefer-for-of
    for (let i = 0; i < this.legendData[index].datasetIndices.length; i++) {
      const datasetIndex = this.legendData[index].datasetIndices[i];
      this.chart.datasets[datasetIndex].hidden = !this.checked[index];
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

    this.loading = true;
    const repositoryIds = Array.from(this.repositories.keys());
    this.getBenchmarkingResults(repositoryIds);

    this.deleteLine();
  }

  private getBenchmarkingResults(repositoryIds: number[]) {
    if (repositoryIds.length === 0) {
      // remove null object
      this.datasets = this.datasets.splice(1);
      this.resetZoom();
      this.chart.update();
      this.loadProperty();
      return;
    }
    this.benchmarkingResultService.getBenchmarkingResults(this.selectedBenchmark.id, repositoryIds[0], 'master').subscribe(
      data => {
        this.repositoryResults.set(repositoryIds[0], data);
        const lines = this.calculateLines(repositoryIds[0]);
        // both is important, otherwise event listening for change of legend gets messed up
        this.chart.datasets.concat(lines);
        this.datasets = this.datasets.concat(lines);
        this.chart.update();
        this.getBenchmarkingResults(repositoryIds.splice(1));
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
    this.loadImages();
  }

  private loadImages() {
    const errorImage = new Image(20, 20);
    const globalErrorImage = new Image(20, 20);
    const notYetImage = new Image(20, 20);
    const noBenchmarks = new Image(20, 20);
    errorImage.src = 'assets/clear.svg';
    globalErrorImage.src = 'assets/block.svg';
    notYetImage.src = 'assets/run.svg';
    noBenchmarks.src = 'assets/disabled.svg';
    Chart.pluginService.register({
      afterUpdate: (chart) => {
        // this method is called globally so make sure it only updates for this chart
        if (this.chart.chart === chart) {
          // tslint:disable-next-line:prefer-for-of
          for (let i = 0; i < chart.config.data.datasets.length; i++) {
            const dataset: any = chart.config.data.datasets[i];
            // key of dataset._meta has a random name
            for (let j = 0; j < dataset._meta[Object.keys(dataset._meta)[0]].data.length; j++) {
              const element = dataset._meta[Object.keys(dataset._meta)[0]].data[j];
              if (!dataset.code[j].result) {
                element._model.pointStyle = notYetImage;
              } else if (dataset.code[j].globalError) {
                element._model.pointStyle = globalErrorImage;
              } else if (Object.keys(dataset.code[j].result).length === 0) {
                element._model.pointStyle = noBenchmarks;
              } else if (dataset.code[j].result[this.selectedBenchmarkProperty.name].errorMessage) {
                element._model.pointStyle = errorImage;
              }
            }
          }
          this.legendData = chart.generateLegend();
        }
      }
    });
    this.chart.update();
    this.loading = false;
  }

  private calculateLines(repositoryId: number): any[] {
    const lines = [];
    const newestCommit = this.getNewestCommit(this.repositoryResults.get(repositoryId));
    this.lists = [];
    this.checked = [];
    const empty = [];
    for (const [, commit] of Object.entries(this.repositoryResults.get(repositoryId))) {
      commit.marked = false;
    }
    if (newestCommit) {
      this.dfs(repositoryId, this.repositoryResults.get(repositoryId)[newestCommit], empty);
    } else {
      console.error('No newest commit found');
    }
    let index = 0;
    for (const list of this.lists) {
      this.checked[index] = true;
      const dataset: Dataset = {
        data: [],
        code: [],
        label: '' + index,
        fill: false,
        lineTension: 0,
        repositoryId,
        repositoryName: this.repositories.get(repositoryId).name,
        branch: 'master',
        borderColor: this.repositories.get(repositoryId).color,
        pointBackgroundColor: this.repositories.get(repositoryId).color,
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

  private dfs(repositoryId: number, current: any, list: any[]) {
    if (!current) {
      this.lists.push(list);
      return;
    }
    list.push(this.repositoryResults.get(repositoryId)[current.commitHash]);
    if (current.parents.length === 0 || current.marked === true || !this.repositoryResults.get(repositoryId)[current.commitHash]) {
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
      if (this.chart && this.chart.chart.canvas.id === instance.canvas.id) {
        instance.options.annotation.annotations = [];
      }
    });

    this.chart.update();
  }

  private legendCallback(currentChart: any): any {
    const map: Map<string, LegendItem> = new Map<string, LegendItem>();
    let index = 0;
    for (const dataset of currentChart.data.datasets) {
      const key = dataset.repositoryId + '/' + dataset.branch;
      if (map.has(key)) {
        map.get(key).datasetIndices.push(index);
      } else {
        map.set(key, {
          repositoryId: dataset.repositoryId,
          repositoryName: dataset.repositoryName,
          branch: dataset.branch,
          datasetIndices: [index] // FIXME: highly likely to cause issues
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
