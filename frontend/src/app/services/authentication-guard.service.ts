import { AuthenticationService } from './authentication.service';
import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';

/**
 * a guard to protect routes that require authentication
 */
@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuardService implements CanActivate {

  constructor(
    public authenticationService: AuthenticationService,
    public router: Router
  ) { }

  /**
   * returns if a user is allowed to access content that requires authentication
   */
  public canActivate(): boolean {
    if (this.authenticationService.isLoggedOut()) {
      this.router.navigate(['login']);
      return false;
    }
    return true;
  }
}
