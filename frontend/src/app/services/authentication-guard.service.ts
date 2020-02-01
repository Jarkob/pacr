import { AuthenticationService } from './authentication.service';
import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuardService implements CanActivate {

  constructor(
    public authenticationService: AuthenticationService,
    public router: Router
  ) { }

  public canActivate(): boolean {
    if (this.authenticationService.isLoggedOut()) {
      this.router.navigate(['login']);
      return false;
    }
    return true;
  }
}
