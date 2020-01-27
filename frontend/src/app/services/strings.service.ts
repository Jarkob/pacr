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

}
