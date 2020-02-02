import { Subscription, interval } from 'rxjs';
import { MatSnackBar } from '@angular/material';
import { SshConfigService } from './../services/ssh-config.service';
import { StringService } from './../services/strings.service';
import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-ssh-config',
  templateUrl: './ssh-config.component.html',
  styleUrls: ['./ssh-config.component.css']
})
export class SshConfigComponent implements OnInit, OnDestroy {

  constructor(
    private stringService: StringService,
    private sshService: SshConfigService,
    private snackBar: MatSnackBar
  ) { }

  private updateSSHKeySubscription: Subscription;
  private sshKeyUpdateInterval = 60; // in seconds

  strings: any;
  publicSSHKey: string;

  ngOnInit() {
    this.stringService.getSSHConfigStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
    this.updateSSHKeySubscription = interval(this.sshKeyUpdateInterval * 1000).subscribe(
      val => {
        this.sshService.getPublicSSHKey().subscribe(
          data => {
            this.publicSSHKey = data;
          }
        );
      }
    );

    this.sshService.getPublicSSHKey().subscribe(
      data => {
        this.publicSSHKey = data;
      }
    );
  }

  copyTextToClipboard(inputElement) {
    inputElement.select();
    document.execCommand('copy');
    inputElement.setSelectionRange(0, 0);

    this.openSnackBar(this.strings.copiedToClipboard);
  }

  openSnackBar(message: string) {
    const snackBarDuration = 2000;

    this.snackBar.open(message, this.strings.snackBarAction, {
      duration: snackBarDuration,
    });
  }

  public sendPrivateSSHKey() {
    this.sshService.sendPrivateSSHKeyToBenchmarkers().subscribe(
      data => {
        this.openSnackBar(this.strings.sendSuccess);
      },
      err => {
        this.openSnackBar(this.strings.snackBarError);
      }
    );
  }

  ngOnDestroy() {
    this.updateSSHKeySubscription.unsubscribe();
  }

}
