import { AuthenticationService } from './../services/authentication.service';
import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';

/**
 * the toolbar of the application
 */
@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

  constructor(
    private location: Location,
    private authenticationService: AuthenticationService
  ) { }


  ngOnInit() {
  }

  /**
   * check if the user is logged in
   */
  public isLoggedIn(): boolean {
    return this.authenticationService.isLoggedIn();
  }

  /**
   * log the user out
   */
  public logout() {
    this.authenticationService.logout();
  }

  /**
   * navigate to the previous page
   */
  public navigateBack() {
    this.location.back();
  }

  /**
   * navigate to the next page
   */
  public navigateForward() {
    this.location.forward();
  }
}
