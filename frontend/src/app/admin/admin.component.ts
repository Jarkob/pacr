import { StringService } from './../services/strings.service';
import { DetailViewMaximizerService } from './../detail-view/detail-view-maximizer.service';
import { DetailViewMaximizedRef } from './../detail-view/detail-view-maximized-ref';
import { SystemEnvironment } from './../classes/system-environment';
import { RepositoryService } from './../services/repository.service';
import { ImportExportService } from './../services/import-export.service';
import { BenchmarkService } from './../services/benchmark.service';
import { Component, OnInit } from '@angular/core';
import { SchedulerService } from '../services/scheduler.service';
import { Repository } from '../classes/repository';
import { Job } from '../classes/job';

/**
 * a component to administrate the application
 */
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
    private schedulerService: SchedulerService,
    private previewDialog: DetailViewMaximizerService,
    private stringService: StringService
  ) { }

  strings: any;

  pullInterval: number;
  deletionInterval: number;
  repositories: Repository;
  systemEnvironment: SystemEnvironment;
  jobs: Job[];

  ngOnInit() {
    this.stringService.getAdminInterfaceStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
  }

  public updateIntervals(): void {
  }

  public openCommitDetailView(hash: string) {
    const dialogRef: DetailViewMaximizedRef = this.previewDialog.open({
      commitHash: hash
    });
  }

}
