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
   * get the events for leaderboards
   */
  public getLeaderboardEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.globalService.url + '/events/leaderboard');
  }

  /**
   * get the events for benchmarking
   */
  public getBenchmarkingEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.globalService.url + '/events/benchmark');
  }

  /**
   * get the commit history
   */
  public getCommitHistory(): Observable<CommitBenchmarkingResult[]> {
    return this.http.get<CommitBenchmarkingResult[]>(this.globalService.url + '/history');
  }
}
