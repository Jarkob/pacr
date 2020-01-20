import { Component, OnInit } from '@angular/core';
import { BenchmarkingResultService } from '../services/benchmarking-result.service';
import { MockService } from '../services/mock.service';
import 'chartjs-plugin-zoom';

@Component({
  selector: 'app-diagram',
  templateUrl: './diagram.component.html',
  styleUrls: ['./diagram.component.css']
})
export class DiagramComponent implements OnInit {


  constructor(
    private benchmarkingResultService: BenchmarkingResultService
  ) { }

  public timeFormat = 'DD/MM/YYYY';

  public options = {
    maintainAspectRatio: false,
    scales: {
      xAxes: [{
          type: 'time',
          time: {
              format: this.timeFormat,
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
    {data: [{x: '04/01/2014', y: 5}, {x: '30/03/2014', y: 3}, {x: '04/01/2015', y: 1}], label: 'repo 1', fill: false},
    {data: [{x: '01/04/2014', y: 2}, {x: '01/10/2014', y: 2}, {x: '01/10/2015', y: 3}], label: 'repo 2', fill: false},
    {data: [{x: '01/01/2014', y: 3}, {x: '02/02/2015', y: 2}], label: 'repo 3', fill: false}
  ];

  public lines = [];

  ngOnInit() {
  }

}
