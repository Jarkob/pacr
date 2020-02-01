import { DiagramOutputResult } from './../classes/diagram-output-result';
import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { MockService } from './mock.service';
import { GlobalService } from './global.service';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BenchmarkingResult } from '../classes/benchmarking-result';
import { Observable } from 'rxjs';
import { Commit } from '../classes/commit';

@Injectable({
  providedIn: 'root'
})
export class BenchmarkingResultService {

  constructor(
    private http: HttpClient,
    private globalService: GlobalService,
    private mockService: MockService
  ) { }

  /**
   * get all benchmarking results for one commit
   * @param sha the commit hash
   */
  public getBenchmarkingResultsForCommit(sha: string): Observable<CommitBenchmarkingResult> {
    return this.http.get<CommitBenchmarkingResult>(this.globalService.url + '/results/commit/' + sha);
  }

  /**
   * get all benchmarking results for one benchmark, one repository and one branch
   * @param benchmark the name of the benchmark
   * @param repository the id of the repository
   * @param branch the branch
   */
  public getBenchmarkingResults(benchmark: number, repositoryId: number, branch: string): Observable<DiagramOutputResult[]> {
    return this.http.get<DiagramOutputResult[]>(this.globalService.url + '/results/' + benchmark + '/' + repositoryId + '/' + branch);
    // return this.mockService.getBenchmarkingResults();
  }

  /**
   * get all benchmarking results for a specific benchmark from a specific repository
   * @param benchmarkId the id of the benchmark
   * @param repositoryId the id of the repository
   */
  public getForBenchmarkAndRepository(benchmarkId: number, repositoryId: number): Observable<any> {
    return this.http.get<any>(this.globalService.url + '/results/benchmark/' + benchmarkId + '/' + repositoryId);
  }

  /**
   * get all new benchmarking results
   */
  public getNewBenchmarkingResults(): Observable<CommitBenchmarkingResult[]> {
    return this.http.get<CommitBenchmarkingResult[]>(this.globalService.url + '/history');
  }

  /**
   * delete all benchmarking results for one commit
   * @param sha the commit hash
   */
  public deleteBenchmarkingResult(sha: string): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/results/commit/' + sha, null);
  }

}
