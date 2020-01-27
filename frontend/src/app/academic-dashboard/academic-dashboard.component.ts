import { DetailViewMaximizerService } from './../detail-view/detail-view-maximizer.service';
import { DetailViewMaximizedRef } from './../detail-view/detail-view-maximized-ref';
import { Component, OnInit } from '@angular/core';

/**
 * a dashboard for single repository use cases
 */
@Component({
  selector: 'app-academic-dashboard',
  templateUrl: './academic-dashboard.component.html',
  styleUrls: ['./academic-dashboard.component.css']
})
export class AcademicDashboardComponent implements OnInit {

  constructor(
    private previewDialog: DetailViewMaximizerService,
  ) { }

  ngOnInit(): void {
  }

  public openCommitDetailView(hash: string) {
    const dialogRef: DetailViewMaximizedRef = this.previewDialog.open({
      commitHash: hash
    });
  }

}
