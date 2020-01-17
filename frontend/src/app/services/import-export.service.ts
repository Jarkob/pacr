import { BenchmarkingResult } from './../classes/benchmarking-result';
import { GlobalService } from './global.service';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ImportExportService {

  constructor(
    private http: HttpClient,
    private globalService: GlobalService
  ) { }

  /**
   * import benchmarking results
   * @param results the results to be imported
   */
  public import(results: BenchmarkingResult[]): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/import', results, httpOptions);
  }

  /**
   * export benchmarking results
   */
  public export(): Observable<BenchmarkingResult[]> {
    return this.http.get<BenchmarkingResult[]>(this.globalService.url + '/export');
  }
}
