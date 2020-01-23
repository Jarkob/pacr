import { OverlayRef } from '@angular/cdk/overlay';

export class DiagramMaximizedRef {

  constructor(private overlayRef: OverlayRef) { }

  close(): void {
    this.overlayRef.dispose();
  }

}
