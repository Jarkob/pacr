import { MockService } from './../services/mock.service';
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
        // Container for pan options
        pan: {
            // Boolean to enable panning
            enabled: true,

            // Panning directions. Remove the appropriate direction to disable
            // Eg. 'y' would only allow panning in the y direction
            mode: 'xy',

            // Function called while the user is panning
            onPan({chart}) { console.log(`I'm panning!!!`); },
            // Function called once panning is completed
            onPanComplete({chart}) { console.log(`I was panned!!!`); }
        },

        // Container for zoom options
        zoom: {
            // Boolean to enable zooming
            enabled: true,
            drag: true,

            // Zooming directions. Remove the appropriate direction to disable
            // Eg. 'y' would only allow zooming in the y direction
            mode: 'xy',

            rangeMin: {
              // Format of min zoom range depends on scale type
              x: null,
              y: null
            },
            rangeMax: {
              // Format of max zoom range depends on scale type
              x: null,
              y: null
            },

            // Speed of zoom via mouse wheel
            // (percentage of zoom on a wheel event)
            speed: 0.1,

            // Function called while the user is zooming
            onZoom({chart}) { console.log(`I'm zooming!!!`); },
            // Function called once zooming is completed
            onZoomComplete({chart}) { console.log(`I was zoomed!!!`); }
        }
      }
    }
  };
  public labels = ['2006', '2007', '2008', '2009', '2010', '2011', '2012'];
  public type = 'line';
  public legend = true;
  public datasets = [
    {data: [65, 59, 80, 81, 56, 55, 40], label: 'Series A', fill: 'false'},
    {data: [28, 48, 40, 19, 86, 27, 90], label: 'Series B', fill: 'false'}
  ];

  ngOnInit() {
    this.benchmarkingResultService.getBenchmarkingResultsFromRepository('test').subscribe(
      data => {
        this.lines.push(data);
      }
    );

    this.mockService.getCompPrakData().subscribe(
      data => {
        let max = 0;
        data.forEach(repo => {
          this.datasets.push(this.getDataByBenchmark(repo, 'Build'));
          max = Math.max(max, this.datasets[this.datasets.length - 1].data.length);
        });
        for (let i = 0; i < max; i++) {
          this.labels.push(i);
        }

        const length = this.datasets.length;
        const step = 0xffffff / length;
        for (const dataset of this.datasets) {
          dataset.borderColor = 'rgba(' + (Math.random() * 255) + ', ' + (Math.random() * 255) + ', ' + (Math.random() * 255) + ',1.0)';
        }
      }
    );
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
