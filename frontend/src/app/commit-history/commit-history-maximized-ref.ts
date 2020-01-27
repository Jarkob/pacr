import { CommitHistoryComponent } from './commit-history.component';
import { OverlayRef } from '@angular/cdk/overlay';

export class CommitHistoryMaximizedRef {

  constructor(
    private overlayRef: OverlayRef,
    private historyComponent: CommitHistoryComponent
    ) { }

  close(): void {
    this.overlayRef.dispose();
  }

  selectCommit(commitHash: string) {
    this.historyComponent.selectCommit(commitHash);
    this.close();
  }

}
