import { Event } from './../classes/event';
import { GlobalService } from './global.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Commit } from '../classes/commit';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  constructor(
    private globalService: GlobalService,
    private http: HttpClient
  ) { }

  public getLeaderboardEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.globalService.url + '/events/leaderboard');
  }

  public getBenchmarkingEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.globalService.url + '/events/benchmarking');
  }

  public getCommitHistory(): Observable<Commit[]> {
    return this.http.get<Commit[]>(this.globalService.url + '/commit-history');
  }
}
