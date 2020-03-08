import { Subscription } from 'rxjs';
import { DetailViewService } from './../services/detail-view.service';
import { DetailViewMaximizerService } from './../detail-view/detail-view-maximizer.service';
import { DetailViewMaximizedRef } from './../detail-view/detail-view-maximized-ref';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-detail-view-comparison-wrapper',
  templateUrl: './detail-view-comparison-wrapper.component.html',
  styleUrls: ['./detail-view-comparison-wrapper.component.css']
})
export class DetailViewComparisonWrapperComponent implements OnInit {

  constructor(
    private detailViewService: DetailViewService
  ) { }

  selectedCommitSubscription: Subscription;
  commitHash: string;

  ngOnInit() {
    this.selectedCommitSubscription = this.detailViewService.selectedCommit.subscribe(
      data => {
        this.commitHash = data;
      }
    );
  }

  /**
   * maximize the detail view
   */
  public maximizeDetailView() {
    this.detailViewService.openMaximizedDetailView(this.commitHash);
  }

}
