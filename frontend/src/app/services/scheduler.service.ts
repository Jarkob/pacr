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
  public getQueue(): Observable<Job[]> {
    return this.http.get<Job[]>(this.globalService.url + '/queue');
  }

  /**
   * priorize a specific job
   * @param job the job to be priorized
   */
  public priorize(job: Job): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/queue', {}, httpOptions);
  }
}
