import { StringService } from './../services/strings.service';
import { DetailViewMaximizerService } from './../detail-view/detail-view-maximizer.service';
import { DetailViewMaximizedRef } from './../detail-view/detail-view-maximized-ref';
import { ImportExportService } from './../services/import-export.service';
import { BenchmarkService } from './../services/benchmark.service';
import { Component, OnInit } from '@angular/core';
import { SchedulerService } from '../services/scheduler.service';

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
    private schedulerService: SchedulerService,
    private previewDialog: DetailViewMaximizerService,
    private stringService: StringService
  ) { }

  strings: any;

  ngOnInit() {
    this.stringService.getAdminInterfaceStrings().subscribe(
      data => {
        this.strings = data;
      }
    );
  }

  /**
   * open the commit detail view
   * @param hash the hash of the commit
   */
  public openCommitDetailView(hash: string) {
    const dialogRef: DetailViewMaximizedRef = this.previewDialog.open({
      commitHash: hash
    });
  }

}
