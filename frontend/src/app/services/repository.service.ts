import { Repository } from './../classes/repository';
import { Observable } from 'rxjs';
import { GlobalService } from './global.service';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Branch } from '../classes/branch';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class RepositoryService {

  constructor(
    private http: HttpClient,
    private globalService: GlobalService
  ) { }

  /**
   * get all repositories
   */
  public getAllRepositories(): Observable<Repository[]> {
    return this.http.get<Repository[]>(this.globalService.url + '/repositories');
  }

  /**
   * get all branches from a specific repository
   * @param repository the repository
   */
  public getBranches(repository: string): Observable<Branch[]> {
    return this.http.get<Branch[]>(this.globalService.url + '/branches/' + repository);
  }

// TODO sensitive methods, require authentication

  /**
   * add a repository to the watchlist
   * @param repository the repository
   */
  public addRepository(repository: string): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/repositories', repository, httpOptions);
  }

  /**
   * update the pull interval
   * @param interval the new pull interval
   */
  public setPullInterval(interval: number): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/interval', interval);
  }

  /**
   * get the pull interval
   */
  public getPullInterval(): Observable<number> {
    return this.http.get<number>(this.globalService.url + '/interval');
  }

  /**
   * remove a repository from the watchlist
   * @param repository the repository
   */
  public removeRepository(repository: string): Observable<{}> {
    return this.http.delete(this.globalService.url + '/repositories/' + repository);
  }

  /**
   * update a repository
   * @param repository the repository
   */
  public updateRepository(repository: Repository): Observable<Repository> {
    return this.http.put<Repository>(this.globalService.url + '/repositories', repository);
  }
}
