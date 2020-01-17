import { SystemEnvironment } from './../classes/system-environment';
import { RepositoryService } from './../services/repository.service';
import { ImportExportService } from './../services/import-export.service';
import { BenchmarkService } from './../services/benchmark.service';
import { Component, OnInit } from '@angular/core';
import { SchedulerService } from '../services/scheduler.service';
import { Repository } from '../classes/repository';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  constructor(
    private benchmarkService: BenchmarkService,
    private importExportService: ImportExportService,
    private repositoryService: RepositoryService,
    private schedulerService: SchedulerService
  ) { }

  pullInterval: number;
  deletionInterval: number;
  repositories: Repository;
  systemEnvironment: SystemEnvironment;

  ngOnInit() {
  }

  public editIntervals(): void {
  }
}
