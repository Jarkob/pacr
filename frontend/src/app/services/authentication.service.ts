import { GlobalService } from './global.service';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(
    private http: HttpClient,
    private globalService: GlobalService
  ) { }

  private loggedIn = false;

  /**
   * login user
   * @param password the password
   */
  public login(password: string): Observable<any> {
    return this.http.post<any>(this.globalService.url + '/login', {password})
    .pipe(
      tap(
        data => {
          this.setSession(data);
          this.loggedIn = true;
        }
      )
    );
  }

  /**
   * check if a user is logged in
   */
  public isLoggedIn(): boolean {
    return sessionStorage.getItem('token') != null && this.loggedIn;
  }

  /**
   * check if a user is logged out
   */
  public isLoggedOut(): boolean {
    return !this.isLoggedIn();
  }

  /**
   * logout user
   */
  public logout(): void {
    sessionStorage.removeItem('token');
    this.loggedIn = false;
  }

  private setSession(authResult: any) {
    sessionStorage.setItem('token', authResult.token);
  }
}
