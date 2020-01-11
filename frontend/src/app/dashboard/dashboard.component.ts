import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { Component, OnInit } from '@angular/core';
import 'chartjs-plugin-zoom';
import * as moment from 'moment';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor(
    private benchmarkingResultService: BenchmarkingResultService,
    private mockService: MockService
  ) { }

  public timeFormat = 'DD/MM/YYYY';

  public options = {
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
    {data: [], label: '', fill: false, borderColor: 'rgba(0,0,0,1)' }
  ];

  public lines = [];

  public options = {
    scales: {
        yAxes: [{
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
    {data: [], label: 'repository name', fill: 'false'}
  ];


  ngOnInit() {
  }

  private getDataByBenchmark(data: any, benchmark: string) {
    const result = {data: [], label: '', fill: false, borderColor: 'rgba(0,0,0,0)'};
    for (const key of Object.keys(data.commits)) {
      if (data.commits[key].hasOwnProperty(benchmark)) {
        if (data.commits[key][benchmark].time.hasOwnProperty('results')) {
          result.data.push(data.commits[key][benchmark].time.results[0]);
        }
      }
    }
    result.label = data.name;
    console.log(result);
    return result;
  }

}
