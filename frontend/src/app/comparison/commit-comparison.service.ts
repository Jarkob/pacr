import { COMMIT_HASH_1_DATA, COMMIT_HASH_2_DATA } from './commit-comparison.tokens';
import { CommitComparisonComponent } from './../commit-comparison/commit-comparison.component';
import { CommitComparisonRef } from './commit-comparison-ref';
import { Injectable, Injector, ComponentRef } from '@angular/core';
import { Overlay, OverlayConfig, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal, PortalInjector } from '@angular/cdk/portal';

interface CommitComparisonConfig {
  panelClass?: string;
  hasBackdrop?: boolean;
  commitHash1?: string;
  commitHash2?: string;
}

const DEFAULT_CONFIG: CommitComparisonConfig = {
  hasBackdrop: true,
  panelClass: 'tm-file-preview-dialog-panel',
  commitHash1: null,
  commitHash2: null
};

@Injectable()
export class CommitComparisonService {

  constructor(
    private injector: Injector,
    private overlay: Overlay) { }

  open(config: CommitComparisonConfig = {}) {
    // Override default configuration
    const dialogConfig = { ...DEFAULT_CONFIG, ...config };

    // Returns an OverlayRef which is a PortalHost
    const overlayRef = this.createOverlay(dialogConfig);

    // Instantiate remote control
    const dialogRef = new CommitComparisonRef(overlayRef);

    const overlayComponent = this.attachDialogContainer(overlayRef, dialogConfig, dialogRef);

    overlayRef.backdropClick().subscribe(_ => dialogRef.close());

    return dialogRef;
  }

  private createOverlay(config: CommitComparisonConfig) {
    const overlayConfig = this.getOverlayConfig(config);
    return this.overlay.create(overlayConfig);
  }

  private attachDialogContainer(overlayRef: OverlayRef, config: CommitComparisonConfig, dialogRef: CommitComparisonRef) {
    const injector = this.createInjector(config, dialogRef);

    const containerPortal = new ComponentPortal(CommitComparisonComponent, null, injector);
    const containerRef: ComponentRef<CommitComparisonComponent> = overlayRef.attach(containerPortal);

    return containerRef.instance;
  }

  private createInjector(config: CommitComparisonConfig, dialogRef: CommitComparisonRef): PortalInjector {
    const injectionTokens = new WeakMap();

    injectionTokens.set(CommitComparisonRef, dialogRef);
    injectionTokens.set(COMMIT_HASH_1_DATA, config.commitHash1);
    injectionTokens.set(COMMIT_HASH_2_DATA, config.commitHash2);

    return new PortalInjector(this.injector, injectionTokens);
  }

  private getOverlayConfig(config: CommitComparisonConfig): OverlayConfig {
    const positionStrategy = this.overlay.position()
      .global()
      .centerHorizontally()
      .centerVertically();

    const overlayConfig = new OverlayConfig({
      hasBackdrop: config.hasBackdrop,
      panelClass: config.panelClass,
      scrollStrategy: this.overlay.scrollStrategies.block(),
      positionStrategy
    });

    return overlayConfig;
  }
}
