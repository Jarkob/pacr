import { BenchmarkGroup } from './../classes/benchmark-group';
import { GlobalService } from './global.service';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Benchmark } from '../classes/benchmark';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class BenchmarkService {

  constructor(
    private http: HttpClient,
    private globalService: GlobalService
  ) { }

  /**
   * get all benchmarks
   */
  public getAllBenchmarks(): Observable<Benchmark[]> {
    return this.http.get<Benchmark[]>(this.globalService.url + '/benchmarks');
  }

  /**
   * get all benchmarks from a specific group
   */
  public getBenchmarksByGroup(id: number): Observable<Benchmark[]> {
    return this.http.get<Benchmark[]>(this.globalService.url + '/benchmarks/' + id);
  }

  /**
   * get all benchmark groups
   */
  public getAllGroups(): Observable<BenchmarkGroup[]> {
    return this.http.get<BenchmarkGroup[]>(this.globalService.url + '/groups');
  }

  /**
   * update a benchmark
   * @param benchmark the benchmark to be updated
   */
  public updateBenchmark(benchmark: Benchmark): Observable<Benchmark> {
    return this.http.put<Benchmark>(this.globalService.url + '/benchmarks', benchmark);
  }

  /**
   * add a benchmark group
   * @param name the name of the group
   */
  public addGroup(name: string): Observable<string> {
    return this.http.post<string>(this.globalService.url + '/groups', name, httpOptions);
  }
}
