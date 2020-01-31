import { FullJobQueue } from './../classes/full-job-queue';
import { SchedulerService } from './../services/scheduler.service';
import { Component, OnInit } from '@angular/core';
import { Job } from '../classes/job';
import { Subscription, interval } from 'rxjs';

/**
 * shows the job queue
 */
@Component({
  selector: 'app-job-queue',
  templateUrl: './job-queue.component.html',
  styleUrls: ['./job-queue.component.css']
})
export class JobQueueComponent implements OnInit {

  constructor(
    private schedulerService: SchedulerService
  ) { }

  fullJobQueue: FullJobQueue;
  queueSubscription: Subscription;

  queueUpdateInterval = 5; // in seconds

  ngOnInit() {
    // perform initial load operation
    this.schedulerService.getQueue().subscribe(
      data => {
        this.fullJobQueue = data;
      }
    );

    this.queueSubscription = interval(this.queueUpdateInterval * 1000).subscribe(
      val => {
        this.schedulerService.getQueue().subscribe(
          data => {
            this.fullJobQueue = data;
          }
        );
      }
    );
  }

  public prioritize(job: Job) {
    this.fullJobQueue.jobs.splice(this.fullJobQueue.jobs.indexOf(job, 0), 1);
    this.fullJobQueue.prioritizedJobs.push(job);
    this.schedulerService.prioritize(job).subscribe();
  }

}
