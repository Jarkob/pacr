import { AuthenticationService } from './../services/authentication.service';
import { SchedulerService } from './../services/scheduler.service';
import { Component, OnInit } from '@angular/core';
import { Job } from '../classes/job';
import { Subscription, interval } from 'rxjs';
import { PageEvent } from '@angular/material';

/**
 * Displays a job queue.
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
  prioritizedPageEvent: PageEvent = new PageEvent();

  pageSizeOptions = [5, 10, 15, 20];

  queueSubscription: Subscription;
  queueUpdateInterval = 15; // in seconds

  ngOnInit() {
    this.getQueue();

    this.queueSubscription = interval(this.queueUpdateInterval * 1000).subscribe(
      val => {
        this.getQueue();
      }
    );
  }

  /**
   * check if the user is logged in
   */
  public isLoggedIn(): boolean {
    return this.authenticationService.isLoggedIn();
  }

  private getQueue() {
    this.getJobsQueue(this.jobsPageEvent);
    this.getPrioritizedQueue(this.prioritizedPageEvent);
  }

  /**
   * Fetch all normal queue items.
   *
   * @param pagingEvent pageable event containing paging information.
   * @returns the pagination event.
   */
  public getJobsQueue(event: any): any {
    this.schedulerService.getJobsQueue(event.pageIndex, event.pageSize).subscribe(
      data => {
        this.jobsPage = data;
        this.jobs = data.content;
      }
    );

    return event;
  }

  /**
   * Fetch all prioritized queue items.
   *
   * @param pagingEvent pageable event containing paging information.
   * @returns the pagination event.
   */
  public getPrioritizedQueue(event: any): any {
    this.schedulerService.getPrioritizedQueue(event.pageIndex, event.pageSize).subscribe(
      data => {
        this.prioritizedPage = data;
        this.prioritized = data.content;
      }
    );

    return event;
  }

  /**
   * Prioritize a selected queue item.
   *
   * @param job the job to be prioritized
   */
  public prioritize(job: Job) {
    this.schedulerService.prioritize({jobID: job.jobID, groupTitle: job.jobGroupTitle}).subscribe(
      data => {
        this.getQueue();
      }
    );
  }

}
