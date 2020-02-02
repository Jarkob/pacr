import { FullJobQueue } from './../classes/full-job-queue';
import { Observable } from 'rxjs';
import { GlobalService } from './global.service';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Job } from '../classes/job';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

export interface PrioritizeMessage {
  jobID: string;
  groupTitle: string;
}

@Injectable({
  providedIn: 'root'
})
export class SchedulerService {

  constructor(
    private http: HttpClient,
    private globalService: GlobalService
  ) { }

  /**
   * get the current prioritized queue
   */
  public getPrioritizedQueue(page: number, pageSize: number): Observable<any> {
    return this.http.get<any>(this.globalService.url + '/queue/prioritized?page=' + page + '&size=' + pageSize);
  }

  /**
   * get the current jobs queue
   */
  public getJobsQueue(page: number, pageSize: number): Observable<any> {
    return this.http.get<any>(this.globalService.url + '/queue/jobs?page=' + page + '&size=' + pageSize);
  }

  /**
   * prioritize a specific job
   * @param job the job to be prioritized
   */
  public prioritize(message: PrioritizeMessage): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/prioritize', message, httpOptions);
  }
}
