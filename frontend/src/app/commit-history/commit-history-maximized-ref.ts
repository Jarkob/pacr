import { OverlayRef } from '@angular/cdk/overlay';

export class CommitHistoryMaximizedRef {

  constructor(private overlayRef: OverlayRef) { }

  close(): void {
    this.overlayRef.dispose();
  }

}
