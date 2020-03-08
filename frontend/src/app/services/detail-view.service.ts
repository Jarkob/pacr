import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DetailViewMaximizedRef } from '../detail-view/detail-view-maximized-ref';
import { DetailViewMaximizerService } from '../detail-view/detail-view-maximizer.service';

@Injectable({
  providedIn: 'root'
})
export class DetailViewService {

  selectedCommit = new BehaviorSubject<string>('');

  constructor(
    private previewDialog: DetailViewMaximizerService
  ) { }

  /**
   * select a commit
   * @param commitHash the hash of the commit to be selected
   */
  public selectCommit(commitHash: string) {
    this.selectedCommit.next(commitHash);
  }

  /**
   * Opens a maximized version of the commit detail view and globally
   * selects the given commit for all detail views.
   * @param commitHash the commit hash which is selected
   */
  public openMaximizedDetailView(commitHash: string) {
    this.selectCommit(commitHash);

    const dialogRef: DetailViewMaximizedRef = this.previewDialog.open({
      commitHash: commitHash
    });
  }
}
