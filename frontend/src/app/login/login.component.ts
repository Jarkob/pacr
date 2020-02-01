import { AuthenticationService } from './../services/authentication.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

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
    private router: Router
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
   * login a user
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
            console.log('Login successful');
          },
          err => {
            console.error('Error: ', err);
            this.loading = false;
          }
        );
    }
  }
}
