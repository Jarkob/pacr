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

  public getLeaderboardEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.globalService.url + '/events/leaderboard');
  }

  public getBenchmarkingEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.globalService.url + '/events/benchmarking');
  }
}
