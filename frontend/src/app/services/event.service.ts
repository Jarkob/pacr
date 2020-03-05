import { CommitHistoryItem } from './../classes/commit-history-item';
import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { Event } from './../classes/event';
import { GlobalService } from './global.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  constructor(
    private globalService: GlobalService,
    private http: HttpClient
  ) { }

  /**
   * get the events for benchmarking
   */
  public getBenchmarkingEvents(page: number, pageSize: number): Observable<any> {
    return this.http.get<Event[]>(this.globalService.url + '/events/benchmark?page=' + page + '&size=' + pageSize);
  }

  /**
   * get the commit history
   */
  public getCommitHistory(page: number, pageSize: number): Observable<any> {
    return this.http.get<CommitHistoryItem[]>(this.globalService.url + '/history?page=' + page + '&size=' + pageSize);
  }
}
