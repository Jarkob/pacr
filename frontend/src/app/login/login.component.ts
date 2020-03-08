import { ErrorComponent } from './../error/error.component';
import { AuthenticationService } from './../services/authentication.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material';

/**
 * shows a form for login
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(
    private fb: FormBuilder,
    private authenticationService: AuthenticationService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.form = this.fb.group({password: ['', Validators.required]});
  }

  form: FormGroup;
  loading = false;
  returnUrl = '/admin';

  ngOnInit() {
    this.authenticationService.logout();
  }

  /**
   * log a user in
   */
  public login(): void {
    this.loading = true;
    const val = this.form.value;
    if (val.password) {
      this.authenticationService.login(val.password)
        .subscribe(
          () => {
            this.loading = false;
            this.router.navigateByUrl(this.returnUrl);
          },
          err => {
            err.message = "Login Unsuccessful";
            this.dialog.open(ErrorComponent, {data: err});
            this.loading = false;
          }
        );
    }
  }
}
