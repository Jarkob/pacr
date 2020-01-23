import { OverlayRef } from '@angular/cdk/overlay';

export class DetailViewMaximizedRef {

  constructor(private overlayRef: OverlayRef) { }

  close(): void {
    this.overlayRef.dispose();
  }

}
