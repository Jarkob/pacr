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

  public isLoggedIn(): boolean {
    return this.authenticationService.isLoggedIn();
  }

  public navigateBack() {
    this.location.back();
  }

  public navigateForward() {
    this.location.forward();
  }
}
