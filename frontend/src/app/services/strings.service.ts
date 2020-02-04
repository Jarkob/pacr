import { Observable } from 'rxjs';
import { GlobalService } from './global.service';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
providedIn: 'root'
})
export class StringService {

    constructor(
        private http: HttpClient,
        private globalService: GlobalService
    ) { }

    /**
     * get strings for the detail view
     */
    public getDetailViewStrings(): Observable<any> {
        return this.http.get('./assets/detail-view-string.json');
    }

     /**
      * get strings for the event list
      */
    public getEventsStrings(): Observable<any> {
        return this.http.get('./assets/events-string.json');
    }

    /**
     * get strings for the commit history
     */
     public getCommitHistoryStrings(): Observable<any> {
        return this.http.get('./assets/commit-history-string.json');
    }

    /**
     * get strings for the benchmarker list
     */
    public getBenchmarkerListStrings(): Observable<any> {
        return this.http.get('./assets/benchmarker-list-string.json');
    }

    /**
     * get strings for the admin interface
     */
    public getAdminInterfaceStrings(): Observable<any> {
        return this.http.get('./assets/admin-string.json');
    }

    public getAdminRepositoriesStrings(): Observable<any> {
        return this.http.get('./assets/admin-repositories-string.json');
    }

    public getAdminBenchmarksStrings(): Observable<any> {
        return this.http.get('./assets/admin-benchmarks-string.json');
    }

    public getSSHConfigStrings(): Observable<any> {
        return this.http.get('./assets/ssh-config-string.json');
    }

    public getComparisonStrings(): Observable<any> {
        return this.http.get('./assets/comparison-string.json');
    }

    public getCommitComparisonStrings(): Observable<any> {
        return this.http.get('./assets/commit-comparison-string.json');
    }

    public getCommitDetailsStrings(): Observable<any> {
        return this.http.get('./assets/commit-details-string.json');
    }

    public getSystemEnvironmentStrings(): Observable<any> {
        return this.http.get('./assets/system-environment-string.json');
    }

    public getBenchmarkingResultTableStrings(): Observable<any> {
        return this.http.get('./assets/benchmarking-result-table-string.json');
    }

    public getCommitComparisonTableStrings(): Observable<any> {
        return this.http.get('./assets/commit-comparison-table-string.json');
    }
}
