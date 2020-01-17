import { GlobalService } from './global.service';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BenchmarkingResult } from '../classes/benchmarking-result';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BenchmarkingResultService {

  constructor(
    private http: HttpClient,
    private globalService: GlobalService
  ) { }

  /**
   * get all benchmarking results from one repository
   * @param repository the repository
   */
  public getBenchmarkingResultsFromRepository(repository: string): Observable<BenchmarkingResult[]> {
    return this.http.get<BenchmarkingResult[]>(this.globalService.url + '/results/repository/' + repository);
  }

  /**
   * get all benchmarking results from a specific branch
   * @param repository the repository
   * @param branch the branch
   */
  public getBenchmarkingResultsFromBranch(repository: string, branch: string): Observable<BenchmarkingResult[]> {
    return this.http.get<BenchmarkingResult[]>(this.globalService.url + '/results/repository/' + repository + '/' + branch);
  }

  /**
   * get all benchmarking results for one commit
   * @param sha the commit hash
   */
  public getBenchmarkingResultsForCommit(sha: string): Observable<BenchmarkingResult> {
    return this.http.get<BenchmarkingResult>(this.globalService.url + '/results/commit/' + sha);
  }

  /**
   * get all benchmarking results for one benchmark
   * @param name the name of the benchmark
   */
  public getBenchmarkingResultsForBenchmark(name: string): Observable<BenchmarkingResult[]> {
    return this.http.get<BenchmarkingResult[]>(this.globalService.url + '/results/benchmark/' + name);
  }

  /**
   * get all new benchmarking results
   */
  public getNewBenchmarkingResults(): Observable<BenchmarkingResult[]> {
    return this.http.get<BenchmarkingResult[]>(this.globalService.url + '/results/new');
  }
}
