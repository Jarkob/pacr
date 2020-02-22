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
   * @param results the results to be imported in json format
   */
  public import(results: any): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/import-results', results, httpOptions);
  }

  /**
   * export benchmarking results
   */
  public export(): Observable<any> {
    return this.http.get<any>(this.globalService.url + '/export-results');
  }
}
