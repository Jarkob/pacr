import { Observable } from 'rxjs';
import { GlobalService } from './global.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

const httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
providedIn: 'root'
})
export class SshConfigService {

    constructor(
        private http: HttpClient,
        private globalService: GlobalService
    ) { }

    /**
     * get the current job queue
     */
    public getPublicSSHKey(): Observable<string> {
        return this.http.get(this.globalService.url + '/ssh/public-key', {responseType: 'text'});
    }

    /**
     * prioritize a specific job
     * @param job the job to be prioritized
     */
    public sendPrivateSSHKeyToBenchmarkers(): Observable<any> {
        return this.http.get<any>(this.globalService.url + '/ssh/send-to-benchmarkers');
    }
}
