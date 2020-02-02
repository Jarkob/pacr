import { OverlayRef } from '@angular/cdk/overlay';

export class CommitComparisonRef {

  constructor(private overlayRef: OverlayRef) { }

  close(): void {
    this.overlayRef.dispose();
  }

}
