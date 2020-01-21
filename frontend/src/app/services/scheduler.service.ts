import { FullJobQueue } from './../classes/full-job-queue';
import { Observable } from 'rxjs';
import { GlobalService } from './global.service';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Job } from '../classes/job';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class SchedulerService {

  constructor(
    private http: HttpClient,
    private globalService: GlobalService
  ) { }

  /**
   * get the current job queue
   */
  public getQueue(): Observable<FullJobQueue> {
    return this.http.get<FullJobQueue>(this.globalService.url + '/queue');
  }

  /**
   * prioritize a specific job
   * @param job the job to be prioritized
   */
  public prioritize(job: Job): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/queue', job, httpOptions);
  }
}
