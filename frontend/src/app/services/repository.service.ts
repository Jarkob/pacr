import { Repository } from './../classes/repository';
import { Observable } from 'rxjs';
import { GlobalService } from './global.service';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

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
   * get all branches
   */
  public getAllBranches(pullURL: string): Observable<string[]> {
    return this.http.post<string[]>(this.globalService.url + '/branches', pullURL, httpOptions);
  }

// TODO sensitive methods, require authentication

  /**
   * add a repository to the watchlist
   * @param repository the repository
   */
  public addRepository(repository: Repository): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/addRepository', repository, httpOptions);
  }

  /**
   * update the pull interval
   * @param interval the new pull interval
   */
  public setPullInterval(interval: number): Observable<{}> {
    return this.http.post<{}>(this.globalService.url + '/pull-interval', interval);
  }

  /**
   * get the pull interval
   */
  public getPullInterval(): Observable<number> {
    return this.http.get<number>(this.globalService.url + '/pull-interval');
  }

  /**
   * remove a repository from the watchlist
   * @param repository the repository
   */
  public removeRepository(repository: number): Observable<{}> {
    return this.http.delete(this.globalService.url + '/deleteRepository/' + repository);
  }

  /**
   * update a repository
   * @param repository the repository
   */
  public updateRepository(repository: Repository): Observable<Repository> {
    return this.http.put<Repository>(this.globalService.url + '/repositories', repository);
  }
}
