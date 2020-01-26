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
     * get the current job queue
     */
    public getDetailViewStrings(): Observable<any> {
        return this.http.get('./assets/detail-view-string.json');
    }


}
