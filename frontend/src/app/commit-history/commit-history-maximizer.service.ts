import { CommitHistoryMaximizedComponent } from './../commit-history-maximized/commit-history-maximized.component';
import { CommitHistoryMaximizedRef } from './commit-history-maximized-ref';
import { Injectable, Injector, ComponentRef } from '@angular/core';
import { Overlay, OverlayConfig, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal, PortalInjector } from '@angular/cdk/portal';

interface CommitHistoryMaximizedConfig {
  panelClass?: string;
  hasBackdrop?: boolean;
}

const DEFAULT_CONFIG: CommitHistoryMaximizedConfig = {
  hasBackdrop: true,
  panelClass: 'tm-file-preview-dialog-panel'
};

@Injectable()
export class CommitHistoryMaximizerService {

  constructor(
    private injector: Injector,
    private overlay: Overlay) { }

  open(config: CommitHistoryMaximizedConfig = {}) {
    // Override default configuration
    const dialogConfig = { ...DEFAULT_CONFIG, ...config };

    // Returns an OverlayRef which is a PortalHost
    const overlayRef = this.createOverlay(dialogConfig);

    // Instantiate remote control
    const dialogRef = new CommitHistoryMaximizedRef(overlayRef);

    const overlayComponent = this.attachDialogContainer(overlayRef, dialogConfig, dialogRef);

    overlayRef.backdropClick().subscribe(_ => dialogRef.close());

    return dialogRef;
  }

  private createOverlay(config: CommitHistoryMaximizedConfig) {
    const overlayConfig = this.getOverlayConfig(config);
    return this.overlay.create(overlayConfig);
  }

  private attachDialogContainer(overlayRef: OverlayRef, config: CommitHistoryMaximizedConfig, dialogRef: CommitHistoryMaximizedRef) {
    const injector = this.createInjector(config, dialogRef);

    const containerPortal = new ComponentPortal(CommitHistoryMaximizedComponent, null, injector);
    const containerRef: ComponentRef<CommitHistoryMaximizedComponent> = overlayRef.attach(containerPortal);

    return containerRef.instance;
  }

  private createInjector(config: CommitHistoryMaximizedConfig, dialogRef: CommitHistoryMaximizedRef): PortalInjector {
    const injectionTokens = new WeakMap();

    injectionTokens.set(CommitHistoryMaximizedRef, dialogRef);

    return new PortalInjector(this.injector, injectionTokens);
  }

  private getOverlayConfig(config: CommitHistoryMaximizedConfig): OverlayConfig {
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