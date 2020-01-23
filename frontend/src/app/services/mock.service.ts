import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MockService {

  constructor(
    private http: HttpClient
  ) { }

  public getCompPrakData(): Observable<any> {
    return this.http.get('./assets/compprak.json');
  }

  public getLeanData(): Observable<any> {
    return this.http.get('./assets/lean.json');
  }

  public getBenchmarkingResults(): Observable<any> {
    return this.http.get('./assets/mock.json');
  }
}
