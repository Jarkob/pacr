import { SystemEnvironment } from './../classes/system-environment';
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
export class BenchmarkerCommunicationService {

constructor(
    private http: HttpClient,
    private globalService: GlobalService
) { }

    /**
     * get all online benchmarkers
     */
    public getOnlineBenchmarkers(): Observable<SystemEnvironment[]> {
        return this.http.get<SystemEnvironment[]>(this.globalService.url + '/benchmarkers');
    }

}
