import { SystemEnvironment } from './../classes/system-environment';
import { StringService } from './../services/strings.service';
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-system-environment-display',
  templateUrl: './system-environment-display.component.html',
  styleUrls: ['./system-environment-display.component.css']
})
export class SystemEnvironmentDisplayComponent implements OnInit {

  constructor(
    private stringService: StringService
  ) { }

  @Input() systemEnvironment: SystemEnvironment;

  strings: any;

  ngOnInit() {
    this.stringService.getSystemEnvironmentStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
  }

}
