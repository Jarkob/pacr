import { AuthenticationService } from './../services/authentication.service';
import { FullJobQueue } from './../classes/full-job-queue';
import { SchedulerService } from './../services/scheduler.service';
import { Component, OnInit } from '@angular/core';
import { Job } from '../classes/job';
import { Subscription, interval } from 'rxjs';
import { PageEvent } from '@angular/material';

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
    private schedulerService: SchedulerService,
    private authenticationService: AuthenticationService
  ) { }

  jobsPage: any;
  jobs: Job[] = [];
  jobsPageEvent: PageEvent = new PageEvent();

  prioritizedPage: any;
  prioritized: Job[] = [];
  priotitizedPageEvent: PageEvent = new PageEvent();

  pageSizeOptions = [5, 10, 15, 20];

  queueSubscription: Subscription;
  queueUpdateInterval = 60; // in seconds

  ngOnInit() {
    this.getQueue();

    this.queueSubscription = interval(this.queueUpdateInterval * 1000).subscribe(
      val => {
        this.getQueue();
      }
    );
  }

  public isLoggedIn(): boolean {
    return this.authenticationService.isLoggedIn();
  }

  private getQueue() {
    this.getJobsQueue(this.jobsPageEvent);
    this.getPrioritizedQueue(this.priotitizedPageEvent);
  }

  private getJobsQueue(event: any) {
    this.schedulerService.getJobsQueue(event.pageIndex, event.pageSize).subscribe(
      data => {
        this.jobsPage = data;
        this.jobs = data.content;
      }
    );

    return event;
  }

  private getPrioritizedQueue(event: any) {
    this.schedulerService.getPrioritizedQueue(event.pageIndex, event.pageSize).subscribe(
      data => {
        this.prioritizedPage = data;
        this.prioritized = data.content;
      }
    );

    return event;
  }

  public prioritize(job: Job) {
    this.schedulerService.prioritize(job).subscribe(
      data => {
        this.getQueue();
      }
    );
  }

}
